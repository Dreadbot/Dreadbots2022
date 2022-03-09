import numpy as np
import math
import cv2
import os

global_K = np.array([[678.3675545820577, 0.0, 304.74552960651096], 
            [0.0, 677.88787206697, 228.7902426460552], 
            [0.0, 0.0, 1.0]])

negate_x = -1
negate_y = -1

bot = 'crackle' # 'crackle'   'practice'

target_height = 104 #in

if bot == 'practice':
    angle_offset = 23
    roll = 0
    z_offset = target_height - 28.5
elif bot == 'crackle':
    angle_offset = 22
    roll = 0
    z_offset = target_height - 22.325
    relation_factor = 67/22 #deg/px

crosshair_scale = 10


def rotation_matrix(yaw, pitch, roll, single_axis=None):
    yaw = math.radians(yaw)
    pitch = math.radians(pitch)
    roll = math.radians(roll)

    if single_axis is None:
        z_rotation_matrix = np.array([
            [math.cos(yaw), -math.sin(yaw), 0],
            [math.sin(yaw), math.cos(yaw), 0],
            [0, 0, 1]
        ])

        x_rotation_matrix = np.array([
            [1, 0, 0],
            [0, math.cos(pitch), math.sin(pitch)],
            [0, math.sin(pitch), math.cos(pitch)]
        ])

        y_rotation_matrix = np.array([
            [math.cos(roll), 0, math.sin(roll)],
            [0, 1, 0],
            [-math.sin(roll), 0, math.cos(roll)]
        ])

        zx_multiplied = np.matmul(z_rotation_matrix, x_rotation_matrix)
        output_rotation_matrix = np.matmul(zx_multiplied, y_rotation_matrix)

        return output_rotation_matrix

    elif single_axis == "Z":
        z_rotation_matrix = np.array([
            [math.cos(yaw), -math.sin(yaw), 0],
            [math.sin(yaw), math.cos(yaw), 0],
            [0, 0, 1]
        ])

        return z_rotation_matrix

    elif single_axis == "X":
        x_rotation_matrix = np.array([
            [1, 0, 0],
            [0, math.cos(pitch), -math.sin(pitch)],
            [0, math.sin(pitch), math.cos(pitch)]
        ])

        return x_rotation_matrix

    elif single_axis == "Y":
        y_rotation_matrix = np.array([
            [math.cos(roll), 0, math.sin(roll)],
            [0, 1, 0],
            [-math.sin(roll), 0, math.cos(roll)]
        ])

        return y_rotation_matrix

def rotate_2d(vector, theta, origin, round=False):
    theta = math.radians(theta)

    vector = vector - origin

    R = np.array([
        [math.cos(theta), -math.sin(theta)],
        [math.sin(theta),  math.cos(theta)]
    ])

    rotated_vector = R.dot(vector) + origin

    if round:
        rotated_vector[0] = int(rotated_vector[0])
        rotated_vector[1] = int(rotated_vector[1])

    return(rotated_vector)

def similar_triangles_calculation(u, v, angle_offset=angle_offset):
    z_off = z_offset
    z = z_off

    K = global_K
    R = rotation_matrix(0,angle_offset,roll)

    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    cx, cy = 320, 240

    y = (z * fy) / (v - cy)
    x = y * ( (u-cx) / fx)

    cvec = np.array([negate_x*x, negate_y*y, z])
    cvec = R.dot(cvec)
    s = z / cvec[2]

    wvec = cvec * s

    return wvec

def reverse_point(world_vector_camera, round=False): #fix convention later
    K = global_K
    R = rotation_matrix(0,angle_offset,roll)

    world_vector_camera = np.array(world_vector_camera)

    z_off = 81.5

    s = z_off / world_vector_camera[2]

    world_vector_camera /= s

    world_x, world_y, world_z = world_vector_camera
    world_vector_camera = np.array([world_x, world_y, world_z])
    invR = np.linalg.inv(R)
    world_vector_camera = invR.dot(world_vector_camera)
    world_x, world_y, world_z = world_vector_camera

    world_x *= negate_x
    world_y *= negate_y


    fx = K[0, 0]
    fy = K[1, 1]

    cx, cy = 320, 240

    u = fx * world_x/world_y + cx
    v = fy * world_z/world_y + cy


    if round:
        u = int(u)
        v = int(v)

    return (u,v)

def point_center(xs, ys, frame):
    avg_x = sum(xs)/len(xs)
    avg_y = sum(ys)/len(ys)

    K = global_K
    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    cx = 320
    cy = 240

    dy = cy - avg_y
    dx = cx - avg_x

    angle_offset = 14.5
    height = 81.5

    angle_offset_radians = math.radians(angle_offset)
    horizontal_angle = math.degrees(math.atan( (avg_x - cx) / fx ))

    vertical_angle_radians = math.atan(dy/fy) + angle_offset_radians # Vertical Angle

    distance = height/math.tan(vertical_angle_radians)

    cv2.circle(frame, (int(avg_x), int(avg_y)), 5, (100,255,100), thickness=2)



    return(horizontal_angle, distance)

def orth_bisector_calculation(imgpoints, dampen=1.0, prev=None, draw_target=None, visualizer=None):
    # imgpoints = np.array([ [x1,y1], [x2,y2], ... , [xn, yn] ])

    xints = []
    yints = []

    lines = []

    max_v = 0
    for i in range(len(imgpoints)):
        max_v = max([max_v, imgpoints[i][1]])
    
    related_angle = relation_factor*(1/max_v)

    for i in range(len(imgpoints)):
        start_u, start_v = imgpoints[i]
        
        try:
            end_u, end_v = imgpoints[i+1]
        except IndexError:
            break

        sx, sy, _ = similar_triangles_calculation(start_u, start_v, angle_offset=related_angle) #s - START
        ex, ey, _ = similar_triangles_calculation(end_u, end_v, angle_offset=related_angle) #e - END  IM SORRY OK I  LIKE TYPING SHORT VARIABLES SUE ME

        if visualizer is not None:
            visualizer.line((sx, sy), (ex, ey), (0,0,0))

        raw_slope = (sy - ey) / (sx - ex)

        if raw_slope == 0.0:
            orth_slope = 10**4
        else:
            orth_slope = -(1/raw_slope)

        mid_x, mid_y = ((sx + ex)/2, (sy + ey)/2) #start x, y (can treat as origin)

        b = (-orth_slope*mid_x + mid_y)

        row = [orth_slope, b]
        lines.append(row)

    for i in range(len(lines)):
        m1, b1 = lines[i]

        try:
            m2, b2 = lines[i+1]
        except IndexError:
            m2, b2 = lines[0]

        xi = (b2-b1) / (m1-m2)

        yi = m1*xi + b1

        xints.append(xi)
        yints.append(yi)

    if len(xints) == 0:
        return(False, None, None)

    target_x = sum(xints)/len(xints)
    target_y = sum(yints)/len(yints)

    raw_angle = math.degrees(math.atan(target_x/target_y))
    raw_dist = math.sqrt(target_x**2 + target_y**2)

    if dampen is not None:
        prev_angle, prev_dist = prev
        
        target_angle = dampen*raw_angle + (1-dampen)*prev_angle
        target_dist = dampen*raw_dist + (1-dampen)*prev_dist
    else:
        target_angle = raw_angle
        target_dist = raw_dist

    
    target_wvec = (target_x, target_y, z_offset)

    if visualizer is not None:
        visualizer.circle((target_x, target_y), (0,255,0))
        visualizer.line((0,0), (target_x, target_y), (100,0,0))

    cvec = reverse_point(target_wvec, round=True)
    
    if draw_target is not None:
        #crosshairs
        u, v = cvec
        cv2.circle(draw_target, cvec, 3, (255,255,255)) #circle
        cv2.line(draw_target, (u, v+crosshair_scale), (u, v-crosshair_scale), (200,200,200), thickness=3) #vertical crosshair
        cv2.line(draw_target, (u-crosshair_scale, v), (u+crosshair_scale, v), (200,200,200), thickness=3) #horizontal crosshair

    return(True, target_angle, target_dist)

def calibrate_intrinsic(cam_id, img_count=20):
    cap = cv2.VideoCapture(cam_id)

    base_path = "calibrating_imgs"

    for i in range(img_count):
        path = "calibrating_imgs/{0}.png".format(i)
        while True:
            ret, img = cap.read()
            cv2.imshow('frame', img)

            key = cv2.waitKey(1) & 0xFF

            if key == ord('p'):
                cv2.imwrite(path, img)
                break
                
            if key == ord('q'):
                exit()

    CHECKERBOARD = (7,9)

    subpix_criteria = (cv2.TERM_CRITERIA_EPS+cv2.TERM_CRITERIA_MAX_ITER, 30, 0.1)

    objp = np.zeros((1, CHECKERBOARD[0]*CHECKERBOARD[1], 3), np.float32)
    objp[0,:,:2] = np.mgrid[0:CHECKERBOARD[0], 0:CHECKERBOARD[1]].T.reshape(-1, 2)

    _img_shape = None

    objpoints = []
    imgpoints = []

    images = os.listdir(base_path)


    for i in range(0, len(images)):
        images[i] = base_path+"/{0}".format(images[i])

    for file in images:
        img = cv2.imread(file)
        if _img_shape == None:
            _img_shape = img.shape[:2]
        else:
            assert _img_shape == img.shape[:2], "All images must share the same size."

        gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        ret, corners = cv2.findChessboardCorners(gray_img, CHECKERBOARD, cv2.CALIB_CB_ADAPTIVE_THRESH+cv2.CALIB_CB_FAST_CHECK+cv2.CALIB_CB_NORMALIZE_IMAGE)

        if ret == True:
            objpoints.append(objp)
            cv2.cornerSubPix(gray_img,corners,(3,3),(-1,-1),subpix_criteria)
            imgpoints.append(corners)

    N_OK = len(objpoints)

    K = np.zeros((3, 3))
    D = np.zeros((4, 1))

    rvecs = [np.zeros((1, 1, 3), dtype=np.float64) for i in range(N_OK)]
    tvecs = [np.zeros((1, 1, 3), dtype=np.float64) for i in range(N_OK)]

    calibrated_rms = cv2.calibrateCamera(objpoints, imgpoints, gray_img.shape[::-1], K, D, rvecs, tvecs)

    print("Found " + str(N_OK) + " valid images for calibration")
    print("DIM=" + str(_img_shape[::-1]))
    print("K=np.array(" + str(K.tolist()) + ")")
    print("D=np.array(" + str(D.tolist()) + ")")
