import numpy as np
import math
import cv2
import os

# Define sign swaps for projection
negate_x =  1
negate_y =  1
negate_z =  -1

# Current bot in use
bot = 'competition' # 'crackle'   'practice'   'competition'

# Height of the target in inches
target_height = 104 #in

# Pixel center of the image
cam_cx, cam_cy = 320, 240

# Focal length
f = 554

# Default color for drawing
color = (150, 150, 150)

# Known radius of the target in inches
radius = 24

# Set yaw, pitch, roll, and camera height dependent on the bot in use
if bot == 'practice':
    yaw   = 0
    pitch = 22
    roll  = 0

    camera_height = 28.5
elif bot == 'crackle':
    yaw   = 0
    pitch = 14
    roll  = 0

    camera_height = 22.325
elif bot == 'competition':
    yaw   = 8
    pitch = 26
    roll  = 0

    camera_height = 37.5

# Define the z_offset between the camera and the target
z_offset = target_height - camera_height

def rotation_matrix(yaw, pitch, roll):
    # Convert input degrees into radians
    yaw   = math.radians(yaw)
    pitch = math.radians(pitch)
    roll  = math.radians(roll)

    z_rotation_matrix = np.array([ # Calculate the Z-Axis rotation matrix - YAW
        [math.cos(yaw), -math.sin(yaw), 0],
        [math.sin(yaw),  math.cos(yaw), 0],
        [0,              0,             1]
    ])

    x_rotation_matrix = np.array([ # Calculate the X-Axis rotation matrix - PITCH
        [1, 0,               0              ],
        [0, math.cos(pitch), math.sin(pitch)],
        [0, math.sin(pitch), math.cos(pitch)]
    ])

    y_rotation_matrix = np.array([ # Calculate Y-Axis rotation matrix - ROLL
        [ math.cos(roll), 0, math.sin(roll)],
        [ 0,              1, 0             ],
        [-math.sin(roll), 0, math.cos(roll)]
    ])

    """
    NOTE - 3-Dimensional rotation is NON-COMMUTATIVE. This means yaw*pitch*roll != pitch*yaw*roll
           Due to this the input angles MUST be measured in order: YAW->PITCH->ROLL
           If they are measured in the wrong order the matrix will be incorrect and the numbers
           will not be correct
    """

    # Multiply Z & X Axis rotation matrices
    zx_multiplied          = np.matmul(z_rotation_matrix, x_rotation_matrix)
    
    # Multiply the ZX Axis rotation matrix into the Y rotation matrix
    output_rotation_matrix = np.matmul(zx_multiplied, y_rotation_matrix)

    return output_rotation_matrix


# Calculate the 3D rotation matrix based on the calibrated parameters
R     = rotation_matrix(yaw, pitch, roll)

# Calculate the inverse of the rotation matrix, used to "divide" a rotation out of a vector
inv_R = np.linalg.inv(R)


def similar_triangles_calculation(u,v,R=R): # Uses the forward-projection model
    y = 1 # The projection is scaled on the Y value, starting at one

    # Calculate the scaled Z and X values based off the similar triangles
    z = (v - cam_cy) / f
    x = (u - cam_cx) / f

    # Create a "world vector" of the calculated values
    # Some values negated due to sign swap through the origin
    wvec = np.array([negate_x*x, negate_y*y, negate_z*z])

    # Rotate the world vector with the calibrated/calculated rotation matrix
    wvec = R.dot(wvec)

    # Calculate s by deriving a value to multiply the vector by to get Z to be the fixed height of the target
    s = z_offset / wvec[2]

    # Scale the world vector by that scalar
    wvec *= s

    return wvec


def reverse_point(wvec, round=False): # "Undo" the math of similar_triangles_calculation
    # Set the s scalr to the Y value
    s = wvec[1]

    # Scale the world vector down by s
    wvec /= s

    # "Undo" the rotation on the world vector
    wvec = inv_R.dot(wvec)

    # Unwrap world vector values
    wx, wy, wz = wvec

    # Undo sign swapping
    wx *= negate_x
    wy *= negate_y
    wz *= negate_z

    # Calculate the pixel coordinates via similar triangles
    u = (f*wx) + cam_cx
    v = (f*wz) + cam_cy

    # Round if needed; for cv2 drawing
    if round:
        u = int(u)
        v = int(v)

    return (u,v)


def crosshair(draw_target, cvec, halve=True): # Draws a crosshair at the defined point
    # Unwrap the camera vector into u and v
    u, v = cvec

    # Define the target images shape
    h, w, _ = draw_target.shape

    # Color definition code has a divide by zero at the middle
    # If the pixel is in the centerm just add 1 pixel to it
    if u == w//2:
        u += 1

    """
    NOTE - IF FOR SOME REASON THE PI SLOWS DOWN DRAMATICALLY SET THE COLOR TO SOME STATIC COLOR
    """

    # Calculate the changing crosshair gradient
    g = min([ 255/(0.03 * abs(u-320)), 255 ])
    r = 255 - g
    b = 0

    # Most of the drawing done is on the resized down image so by default halve the coordinates
    if halve:
        u = u//2
        v = v//2
    

    color = (b, g, r)
    
    # Draw the crosshair center
    cv2.circle(draw_target, (u, v), 15, color, thickness=2)

    # Draw the crosshair lines
    cv2.line(draw_target,   (u, 0), (u, h), color, thickness=2)
    cv2.line(draw_target,   (0, v), (w, v), color, thickness=2)


def single_point(cvec, draw_target=None): # Calculate angle & distance off one point
    # Unwrap camera vector into u and v
    u, v = cvec

    # Calculate real world coordinate of the single point found
    x, y, _ = similar_triangles_calculation(u, v)

    # Calculate the angle and distance to the single point (distance formula & right angle trig)
    distance         = math.sqrt(x**2 + y**2)
    horizontal_angle = math.degrees(math.atan(x/y))

    # Draw the single point on the image
    if draw_target is not None:
        crosshair(draw_target, cvec)

    return(True, horizontal_angle, distance)


def leg_calculation(imgpoints, dampen=1.0, prev=None, draw_target=None, visualizer=None):
    # imgpoints = np.array([ [u1,v1], [u2,v2], ... , [un, vn] ])

    # Initialize empty X and Y point lists
    xs = []
    ys = []

    for i in range(len(imgpoints)): # Loop through each point in imgpoints
        # Set the start coordinate (u,v) to the current point
        start_u, start_v = imgpoints[i]
        
        try:
            # Set the end coordinate (u,v) to the end point
            end_u, end_v = imgpoints[i+1]
        except IndexError: # If the current point is the last point in the list break out of the list
            break

        # Calculate and set the real-world segment start and end points
        start_x, start_y, _ = similar_triangles_calculation(start_u, start_v) #s - START
        end_x,   end_y,   _ = similar_triangles_calculation(end_u,   end_v) #e - END  IM SORRY OK I  LIKE TYPING SHORT VARIABLES SUE ME


        # Draw the segment on the birds-eye visualizer
        if visualizer is not None:
            visualizer.line((start_x, start_y), (end_x, end_y), (0,0,0))

        # Calculate the segment's slope
        raw_slope = (start_y - end_y) / (start_x - end_x)

        # Calculate the orthogonal slope to the segment
        if raw_slope == 0.0: # If the slope is 0 then the slope of a perpendicular line is inf
            orth_slope = 10**4 # Set the perpendicular to some number good enough to act as inf
        else:
            orth_slope = -(1/raw_slope)

        # Calculate the segment's midpoint
        midpt_x, midpt_y = ((start_x + end_x)/2, (start_y + end_y)/2)

        # Calculate the angle of the segment's slope
        slope_angle = math.atan(orth_slope)

        # Calculate the 'legs' off the midpoint using the known radius
        target_x1 =  radius*math.cos(slope_angle) + midpt_x
        target_x2 = -radius*math.cos(slope_angle) + midpt_x

        """
        Note - When this is calculated there is no way to automatically calculate the "correct" one
        so we calculate two points and then test the slope between the midpoint and the calculated point
        The slope of the leg point must match the calculated orthogonal slope

        The absolute value of the leg point slopes will be equal, what matters is the sign

        This problem doesn't apply to the Y value, in no case should we ever use the negative
        Y value found because the target should NEVER be found BEHIND the camera...
        """

        # The orthogonal slope calculated will have many decimal points, so round it down to 
        # 2 decimal points so that it can be compared in a conditional
        test_orthslope = round(orth_slope,2)

        # Calculate the Y value of the target
        target_y = abs(radius*math.sin(slope_angle)) + midpt_y

        # Calculate the leg point slopes to test in a conditional, round to two decimal points
        slope_target_x1 = round( (midpt_y-target_y) / (midpt_x-target_x1) , 2)
        slope_target_x2 = round( (midpt_y-target_y) / (midpt_x-target_x2) , 2)


        # Set final target x value to the correct leg point by comparing the slopes
        if slope_target_x1 == test_orthslope:
            target_x = target_x1

        elif slope_target_x2 == test_orthslope:
            target_x = target_x2

        else: # If neither slope matches break out of the loop, something went wrong
            break # Not quite sure when this would happen but it's here in case
        

        # Draw a small circle on the birds-eye visualizer at the leg point
        if visualizer is not None:
            visualizer.circle((target_x, target_y), (100,50,220))

        # Append the accepted leg point to the point lists
        xs.append(target_x)
        ys.append(target_y)


    # If no leg points were calculated return no point found
    if len(xs) == 0:
        return(False, None, None)

    # Average out the leg point lists into (x,y)
    target_x = sum(xs)/len(xs)
    target_y = sum(ys)/len(ys)

    # Calculate the angle to the averaged target center
    raw_angle = math.degrees(math.atan(target_x/target_y))

    # Calculate the distance to the averaged target center
    target_distance  = math.sqrt(target_x**2 + target_y**2)

    # Adjust the calculated distance with a regressed model
    error_adjustment = 0.001307*(target_distance**1.931)
    target_distance -= error_adjustment

    """
    Note for the future - The regressed model is defined as a*x^b, and the b is 1.93 which
    is eerily close to 2, a square factor. This regression fix is a permanent solution but more than likely
    a step was missed in the original math and this compensates for that.
    """

    # Perform IIR Filtering on the target center & distance to smooth out the output
    if dampen is not None:
        prev_angle, prev_dist = prev
        
        target_angle = dampen*raw_angle + (1-dampen)*prev_angle
        target_dist  = dampen*target_distance + (1-dampen)*prev_dist
    else:
        target_angle = raw_angle
        target_dist  = target_distance

    # Define the target's world vector
    target_wvec = np.array([target_x, target_y, z_offset])


    # Draw the target's world vector on the birds-eye visualizer
    if visualizer is not None:
        visualizer.circle((target_x, target_y), (255,0,0), radius=24, thickness=1)
        visualizer.circle((target_x, target_y), (0,255,0))
        visualizer.line((0,0), (target_x, target_y), (100,0,0))

    # Forward project the target's world vector into a camera vector
    cvec = reverse_point(target_wvec, round=True)

    # Draw the target's camera vector
    if draw_target is not None:
        crosshair(draw_target, cvec)

    return(True, target_angle, target_dist)
