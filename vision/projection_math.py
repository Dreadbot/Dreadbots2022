import numpy as np
import math
import cv2
import os

def rotation_matrix(yaw, pitch, roll, single_axis=None):
    yaw = math.degrees(yaw)
    pitch = math.degrees(pitch)
    roll = math.degrees(roll)

    if single_axis is not None:
        z_rotation_matrix = np.array([
            [math.cos(yaw), -math.sin(yaw), 0],
            [math.sin(yaw), math.cos(yaw), 0],
            [0, 0, 1]
        ])

        x_rotation_matrix = np.array([
            [1, 0, 0],
            [0, math.cos(pitch), -math.sin(pitch)],
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


def camera_to_world(u, v, intrinsic_matrix, rotation_matrix, s_scalar):
    camera_vector = np.array([u,v,1]) * s_scalar

    inverse_intrinsic = np.linalg.inv(intrinsic_matrix)
    inverse_rotation = np.linalg.inv(rotation_matrix)

    intrinsic_rotation = np.matmul(inverse_intrinsic, inverse_rotation)

    world_vector = intrinsic_rotation.dot(camera_vector)

    return(world_vector)


def similar_triangles_calculation(u, v, K, R):
    # angle = math.radians(14)
    z_off = 104-22.5
    z = z_off

    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    y = (z * fy) / (v - cy)
    x = y * ( (u-cx) / fx)

    y *= 0.5

    world_vector_camera = np.array([-x,-y,z])
    # angle = math.radians(np.linalg.norm(world_vector_camera))
    world_vector_world = R.dot(world_vector_camera)
    # print(f"WORLD VECTOR[ {world_vector_world} ]")
    world_vector_world[2] = z_off

    # unit = world_vector_camera / np.linalg.norm(world_vector_camera)

    # world_vector_world = world_vector_camera*math.cos(angle) + np.cross(unit, world_vector_camera)*math.sin(angle) + unit*np.dot(unit, world_vector_camera)*(1-math.cos(angle))


    return world_vector_world


def reverse_point(world_vector, K, R, round=False):
    angle = 14

    world_x, world_y, world_z = world_vector

    world_vector_camera = np.array([-world_x, -world_y, world_z])

    unit = world_vector_camera / np.linalg.norm(world_vector_camera)

    world_vector_world = world_vector_camera*math.cos(angle) + np.cross(unit, world_vector_camera)*math.sin(angle) + unit*np.dot(unit, world_vector_camera)*(1-math.cos(angle))

    world_x, world_y, world_z = world_vector_world

    world_y *= 2

    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    u = fx * world_x/world_y + cx
    v = fy * world_z/world_y + cy

    if round:
        u = int(u)
        v = int(v)

    return (u,v)


def center_distance(point, K, angle_offset=14, height=81.5):
    fy, cy = K[1, 1], K[1, 2]

    _, y = point

    dy = cy - y

    angle_offset_radians = math.radians(angle_offset)

    vertical_angle_radians = math.atan(dy/fy) + angle_offset_radians # Vertical Angle

    center_distance = height/math.tan(vertical_angle_radians)

    return(center_distance)


def geometric_true_center(p1, p2, draw_target=None): #Rework later for u,v,K
    #y1,x1 = p1
    #y2,x2 = p2
    x1,y1 = p1
    x2,y2 = p2


    K=np.array([[678.3675545820577, 0.0, 304.74552960651096], 
            [0.0, 677.88787206697, 228.7902426460552], 
            [0.0, 0.0, 1.0]])


    R = rotation_matrix(0, -14, 0, single_axis="X")

    x1,y1,z = similar_triangles_calculation(x1,y1,K,R)
    x2,y2,_ = similar_triangles_calculation(x2,y2,K,R)

    slope = (x2-x1)/(y2-y1)
    orth_slope = round(-1/slope, 2)

    mx = (x1 + x2) / 2
    my = (y1 + y2) / 2

    r = 48

    try:
        d = math.sqrt( (x2-x1)**2 + (y2-y1)**2 )
        D = math.sqrt(r**2 - 0.25*((x2-x1)**2 + (y2-y1)**2))
    except ValueError:
        return (False, None, None)

    
    cx1 = mx + (2*D/d)*(y1-my)
    cx2 = mx - (2*D/d)*(y1-my)

    cy1 = my + (2*D/d)*(x1-mx)
    cy2 = my - (2*D/d)*(x1-mx)

    ty = max([cy1, cy2])


    cx1_orth_test = round((cx1-mx)/(ty-my), 2)
    cx2_orth_test = round((cx2-mx)/(ty-my), 2)


    if cx1_orth_test == orth_slope:
        tx = cx1
    elif cx2_orth_test == orth_slope:
        tx = cx2
    # else:
    #     return (False, None, None)

    # distance = math.sqrt(tx**2 + ty**2)
    horizontal_angle = math.degrees(math.atan(tx/ty))

    # print(f"\ndist[ {distance} ]")


    target_center = (tx, ty, z)
    target_pixel_center = reverse_point(target_center, K, R)

    distance = center_distance(target_pixel_center, K)

    if draw_target is not None:
        cv2.circle(draw_target, target_pixel_center, 3, (255,0,0), thickness=-1)
    if False: # yes this is bad im sorry okay
        target_center = (tx, ty)

        far_edge = (tx, ty+r)
        close_edge = (tx, ty-r)
        left_edge = (tx-r, ty)
        right_edge = (tx+r, ty)

        draw_pts = [far_edge, close_edge, left_edge, right_edge, target_center]

        for p in range(len(draw_pts)):
            px, py = draw_pts[p]
            
            draw_pts[p] = reverse_point(px, py, z, K, R, round=True)

            print(draw_pts[p])
            # cv2.circle(draw_target, draw_pts[p], 2, (200, 50, 50), thickness=-1)
        
        far, close, left, right, center = draw_pts

        cx, cy = center
        
        major_axis_left = int(math.sqrt( (left[0] - center[0])**2 + (left[1] - center[1])**2 ))
        major_axis_right = int(math.sqrt( (right[0] - center[0])**2 + (right[1] - center[1])**2 ))

        minor_axis_far = int(math.sqrt( (far[0] - center[0])**2 + (far[1] - center[1])**2 ))
        minor_axis_close = int(math.sqrt( (close[0] - center[0])**2 + (close[1] - center[1])**2 ))

        minor_axis = sum([minor_axis_close, minor_axis_far]) // 2
        major_axis = sum([major_axis_left, major_axis_right]) // 2

        thickness = 2

        vertical_angle = -math.degrees( math.atan( (close[0]-center[0]) / (close[1]-center[1]) ) )

        color = (0,255,0)

        hp1, hp2 = (cx-major_axis, cy), (cx+major_axis, cy)

        vertical_points = np.array([
            rotate_2d(np.array([cx, cy-minor_axis]), vertical_angle, (cx, cy), round=True),
            rotate_2d(np.array([cx, cy+minor_axis]), vertical_angle, (cx, cy), round=True)
        ]).astype(int)

        vp1, vp2 = (vertical_points[0,0], vertical_points[0,1]),(vertical_points[1,0], vertical_points[1,1])


        cv2.line(draw_target, hp1, hp2, (0,255,0))
        cv2.line(draw_target, vp1, vp2, (0,255,0))


        cv2.ellipse(draw_target, center, (major_axis, minor_axis), 0, 0, 360, color, thickness=thickness)
        cv2.circle(draw_target, center, 3, (0,255,0), thickness=-1)
    print(f"angle[ {horizontal_angle} ]  dist[ {distance} ]")

    return(True, horizontal_angle, distance)



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
