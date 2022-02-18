import numpy as np
import math
import cv2
import os

def rotation_matrix(yaw, pitch, roll):
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
        [math.cos(roll), 0, -math.sin(roll)],
        [0, 1, 0],
        [math.sin(roll), 0, math.cos(roll)]
    ])

    zx_multiplied = np.matmul(z_rotation_matrix, x_rotation_matrix)
    output_rotation_matrix = np.matmul(zx_multiplied, y_rotation_matrix)

    return output_rotation_matrix

def calibrate_s(paired_points, intrinsic_matrix, rotation_matrix):
    raw_s_results = []

    inverse_intrinsic = np.linalg.inv(intrinsic_matrix)
    inverse_rotation = np.linalg.inv(rotation_matrix)

    # intrinsic_rotation = np.matmul(inverse_rotation, inverse_intrinsic)
    intrinsic_rotation = np.matmul(inverse_intrinsic, inverse_rotation)


    cnt = 0

    for pair in paired_points:
        print("---| Pair - {} |---".format(cnt))
        image_coord_vector = pair[0]
        world_coord_vector = pair[1]

        u, v, i_z = image_coord_vector
        x, z, y = world_coord_vector

        resultant_vector = intrinsic_rotation.dot(image_coord_vector)
        print(f"\nworld vector:  {world_coord_vector}")
        print(f"camera vector: {image_coord_vector}")

        resultant_x, resultant_z, resultant_y = resultant_vector

        print(f"\nImage Z: {i_z}\nTransformed Z:{resultant_z}")

        # s = u/resultant_x
        s = z/resultant_z

        # print("{}\n".format([s_x, s_y, s_]))
        # print("Matrix: {}\n".format(intrinsic_rotation))


        # avg_s = sum([s_x, s_y, s_])/3

        raw_s_results.append(s)

        print(f"\nS Scalar: {s}")

        cnt += 1


    calibrated_s = sum(raw_s_results)/len(raw_s_results)

    print("\n")
    
    return calibrated_s


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

    # world_vector = inverse_rotation.dot(inverse_intrinsic.dot(camera_vector))

    return(world_vector)

def similar_triangles_calculation(u, v, intrinsic_matrix):
    #Need rot in future
    x = 0.0
    y = 0.0

    z = 4.25

    fx, cx = intrinsic_matrix[0, 0], intrinsic_matrix[0, 2]
    fy, cy = -intrinsic_matrix[1, 1], intrinsic_matrix[1, 2]

    y = (z * fy) / (v - cy)
    x = y * ( (u-cx) / fx)

    world_vector = np.array([x, y, z])

    return world_vector


def calculate_true_center(p1, p2, intrinsic_matrix):
    # x1, y1, _ = similar_triangles_calculation(p1[0], p1[1], intrinsic_matrix)
    # x2, y2, _ = similar_triangles_calculation(p2[0], p2[1], intrinsic_matrix)

    x1, y1, _ = p1
    x2, y2, _ = p2

    q = ( -(x1**2) + x2**2 -(y1**2) + y2**2) / 2
    m = (y1-y2)/(x1-x2)

    r = 4

    a_ty = (m**2 + 1)
    b_ty = (2*m*x1 + 2*m*q + 2*y1)
    c_ty = -(r**2 - (x1+q)**2 + y1**2)

    print(f"a[ {a_ty} ]   b[ {b_ty} ]   c[ {c_ty} ]")

    ty_plus = (-b_ty + math.sqrt(b_ty**2 - 4*a_ty*c_ty)) / 2*a_ty
    ty_minus = (-b_ty - math.sqrt(b_ty**2 - 4*a_ty*c_ty)) / 2*a_ty

    a_tx = 1
    b_tx = 2*x1
    c_tx = -r**2 + (y1 + ty_minus)**2 + x1**2

    print(f"a[ {a_tx} ]   b[ {b_tx} ]   c[ {c_tx} ]")

    try:
        tx_plus = (-b_tx + math.sqrt(b_tx**2 - 4*a_tx*c_tx)) / 2*a_tx
    except:
        tx_plus = 1000000
    
    try:
        tx_minus = (-b_tx - math.sqrt(b_tx**2 - 4*a_tx*c_tx)) / 2*a_tx
    except:
        tx_minus = -10000000

    print(f"Y MINUS[ {ty_minus} ]  Y PLUS[ {ty_plus} ]")
    print(f"X MINUS[ {tx_minus} ]  X PLUS[ {tx_plus} ]")

calculate_true_center((3,4,1), (5,2,1), None)


'''
paired_points = np.array([
    [
        [u,v,1], <- IMAGE COORDS
        [x,y,z]  <- WORLD COORDS
    ]
])
'''