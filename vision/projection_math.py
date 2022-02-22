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


def camera_to_world(u, v, intrinsic_matrix, rotation_matrix, s_scalar):
    camera_vector = np.array([u,v,1]) * s_scalar

    inverse_intrinsic = np.linalg.inv(intrinsic_matrix)
    inverse_rotation = np.linalg.inv(rotation_matrix)

    intrinsic_rotation = np.matmul(inverse_intrinsic, inverse_rotation)

    world_vector = intrinsic_rotation.dot(camera_vector)

    return(world_vector)

def similar_triangles_calculation(u, v, K, R):
    angle = math.radians(14)
    z_off = 104-22.5
    z = z_off

    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    y = (z * fy) / (v - cy)
    x = y * ( (u-cx) / fx)

    y *= 0.5

    world_vector_camera = np.array([-x,-y,z])
    world_vector_camera[2] = z_off

    unit = world_vector_camera / np.linalg.norm(world_vector_camera)

    world_vector_world = world_vector_camera*math.cos(angle) + np.cross(unit, world_vector_camera)*math.sin(angle) + unit*np.dot(unit, world_vector_camera)*(1-math.cos(angle))

    return world_vector_world

def reverse_point(x, y, z, K, R):
    angle = math.radians(-14)

    world_vector_camera = np.array([x, y, z])

    unit = world_vector_camera / np.linalg.norm(world_vector_camera)

    world_vector_world = world_vector_camera*math.cos(angle) + np.cross(unit, world_vector_camera)*math.sin(angle) + unit*np.dot(unit, world_vector_camera)*(1-math.cos(angle))

    x, y, z = world_vector_world

    y *= 2

    fx, cx = K[0, 0], K[0, 2]
    fy, cy = K[1, 1], K[1, 2]

    u = abs(fx * (x/y) + cx)
    v = abs(fy * (z/y) + cy)

    return (u,v)


def geometric_true_center(p1, p2): #Rework later for u,v,K
    # y1,x1 = p1
    # y2,x2 = p2
    x1,y1 = p1
    x2,y2 = p2

    K=np.array([[678.3675545820577, 0.0, 304.74552960651096], 
            [0.0, 677.88787206697, 228.7902426460552], 
            [0.0, 0.0, 1.0]])


    R = rotation_matrix(0, -14, 0, single_axis="X")

    x1,y1,_ = similar_triangles_calculation(x1,y1,K,R)
    x2,y2,_ = similar_triangles_calculation(x2,y2,K,R)

    slope = (y2-y1)/(x2-x1)
    orth_slope = round(-1/slope, 2)

    mx = (x1 + x2) / 2
    my = (y1 + y2) / 2

    r = 24

    try:
        d = math.sqrt( (x2-x1)**2 + (y2-y1)**2 )
        D = math.sqrt(r**2 - 0.25*((x2-x1)**2 + (y2-y1)**2))
    except ValueError:
        print("Circle Fail")
        return None

    
    cx1 = mx + (2*D/d)*(y1-my)
    cx2 = mx - (2*D/d)*(y1-my)

    cy1 = my + (2*D/d)*(x1-mx)
    cy2 = my - (2*D/d)*(x1-mx)

    tx = max([cx1, cx2])

    cy1_orth_test = round((cy1-my)/(tx-mx), 2)
    cy2_orth_test = round((cy2-my)/(tx-mx), 2)

    if cy1_orth_test == orth_slope:
        ty = cy1
    elif cy2_orth_test == orth_slope:
        ty = cy2
    else:
        return None

    distance = math.sqrt(tx**2 + ty**2)
    angle = math.degrees(math.atan(tx/ty)) # If this number is weird try swapping x and y, I got flipped around at some point


    return(angle, distance)

# Performs an exponential smoothing operation (Holt Linear) to filter out high-frequency relative angle noise.
def filter(current_observation, previous_smoothed_statistic, smoothing_factor):
    # Smoothing factor clamp [-1.0, 1.0]
    if smoothing_factor > 1:
        smoothing_factor = 1
    if smoothing_factor < 0:
        smoothing_factor = 0
    
    # Performs the exponential smoothing
    smoothed_statistic = smoothing_factor * current_observation
    smoothed_statistic += (1 - smoothing_factor) * previous_smoothed_statistic

    return smoothed_statistic