import numpy as np
import math
import cv2
import os

global_K = np.array([[678.3675545820577, 0.0, 304.74552960651096], 
            [0.0, 677.88787206697, 228.7902426460552], 
            [0.0, 0.0, 1.0]])

negate_x =  1
negate_y =  1
negate_z = -1

bot = 'competition' # 'crackle'   'practice'   'competition'

target_height = 104 #in

cam_cx, cam_cy = 320, 240

f = 554

color = (150, 150, 150)

radius = 24

if bot == 'practice':
    yaw   = 0
    pitch = 22
    roll  = 0

    z_offset = target_height - 28.5
elif bot == 'crackle':
    yaw   = 0
    pitch = 14
    roll  = 0

    z_offset = target_height - 22.325
elif bot == 'competition':
    yaw   = 8
    pitch = 26
    roll  = 0

    z_offset = target_height - 37.5


def rotation_matrix(yaw, pitch, roll):
    yaw   = math.radians(yaw)
    pitch = math.radians(pitch)
    roll  = math.radians(roll)

    z_rotation_matrix = np.array([
        [math.cos(yaw), -math.sin(yaw), 0],
        [math.sin(yaw),  math.cos(yaw), 0],
        [0,              0,             1]
    ])

    x_rotation_matrix = np.array([
        [1, 0,               0              ],
        [0, math.cos(pitch), math.sin(pitch)],
        [0, math.sin(pitch), math.cos(pitch)]
    ])

    y_rotation_matrix = np.array([
        [ math.cos(roll), 0, math.sin(roll)],
        [ 0,              1, 0             ],
        [-math.sin(roll), 0, math.cos(roll)]
    ])

    zx_multiplied          = np.matmul(z_rotation_matrix, x_rotation_matrix)
    output_rotation_matrix = np.matmul(zx_multiplied, y_rotation_matrix)

    return output_rotation_matrix


R     = rotation_matrix(yaw, pitch, roll)
inv_R = np.linalg.inv(R)


def similar_triangles_calculation(u,v):
    y = 1

    z = (v - cam_cy) / f
    x = (u-cam_cx)   / f

    wvec = np.array([negate_x*x, negate_y*y, negate_z*z])
    wvec = R.dot(wvec)

    s = z_offset / wvec[2]

    wvec *= s

    return wvec


def reverse_point(wvec, round=False):
    s = wvec[1]
    wvec /= s

    wvec = inv_R.dot(wvec)

    wx, wy, wz = wvec

    wx *= negate_x
    wy *= negate_y
    wz *= negate_z

    u = (f*wx) + cam_cx
    v = (f*wz) + cam_cy

    if round:
        u = int(u)
        v = int(v)

    return (u,v)


def crosshair(draw_target, cvec, halve=True):
    u, v = cvec

    h, w, _ = draw_target.shape

    if u == 320:
        u += 1

    g = min([ 255/(0.03 * abs(u-320)), 255 ])
    r = 255 - g
    b = 0

    if halve:
        u = u//2
        v = v//2
    

    color = (b, g, r)
    
    cv2.circle(draw_target, (u, v), 15, color, thickness=2)

    cv2.line(draw_target,   (u, 0), (u, h), color, thickness=2)
    cv2.line(draw_target,   (0, v), (w, v), color, thickness=2)


def single_point(pt, draw_target=None):
    u, v = pt

    x, y, z = similar_triangles_calculation(u, v)

    distance         = math.sqrt(x**2 + y**2)
    horizontal_angle = math.degrees(math.atan(x/y))

    if draw_target is not None:
        crosshair(draw_target, pt)

    return(True, horizontal_angle, distance)


def leg_calculation(imgpoints, dampen=1.0, prev=None, draw_target=None, visualizer=None):
    # imgpoints = np.array([ [u1,v1], [u2,v2], ... , [un, vn] ])

    xs = []
    ys = []

    for i in range(len(imgpoints)):
        start_u, start_v = imgpoints[i]
        
        try:
            end_u, end_v = imgpoints[i+1]
        except IndexError:
            break


        start_x, start_y, _ = similar_triangles_calculation(start_u, start_v) #s - START
        end_x,   end_y,   _ = similar_triangles_calculation(end_u,   end_v) #e - END  IM SORRY OK I  LIKE TYPING SHORT VARIABLES SUE ME


        if visualizer is not None:
            visualizer.line((start_x, start_y), (end_x, end_y), (0,0,0))


        raw_slope = (start_y - end_y) / (start_x - end_x)

        if raw_slope == 0.0:
            orth_slope = 10**4
        else:
            orth_slope = -(1/raw_slope)


        midpt_x, midpt_y = ((start_x + end_x)/2, (start_y + end_y)/2)


        slope_angle = math.atan(orth_slope)


        target_x1 =  radius*math.cos(slope_angle) + midpt_x
        target_x2 = -radius*math.cos(slope_angle) + midpt_x


        test_orthslope = round(orth_slope,2)

        target_y = abs(radius*math.sin(slope_angle)) + midpt_y

        slope_target_x1 = round( (midpt_y-target_y) / (midpt_x-target_x1) , 2)
        slope_target_x2 = round( (midpt_y-target_y) / (midpt_x-target_x2) , 2)


        if slope_target_x1 == test_orthslope:
            tx = target_x1

        elif slope_target_x2 == test_orthslope:
            tx = target_x2

        else:
            break
        

        if visualizer is not None:
            visualizer.circle((tx, target_y), (100,50,220))


        xs.append(tx)
        ys.append(target_y)


    if len(xs) == 0:
        return(False, None, None)


    target_x = sum(xs)/len(xs)
    target_y = sum(ys)/len(ys)


    raw_angle = math.degrees(math.atan(target_x/target_y))
    target_distance  = math.sqrt(target_x**2 + target_y**2)

    error_adjustment = 0.001307*(target_distance**1.931)

    target_distance -= error_adjustment


    if dampen is not None:
        prev_angle, prev_dist = prev
        
        target_angle = dampen*raw_angle + (1-dampen)*prev_angle
        target_dist  = dampen*target_distance + (1-dampen)*prev_dist
    else:
        target_angle = raw_angle
        target_dist  = target_distance

    
    target_wvec = np.array([target_x, target_y, z_offset])


    if visualizer is not None:
        visualizer.circle((target_x, target_y), (255,0,0), radius=24, thickness=1)
        visualizer.circle((target_x, target_y), (0,255,0))
        visualizer.line((0,0), (target_x, target_y), (100,0,0))


    cvec = reverse_point(target_wvec, round=True)

    if draw_target is not None:
        crosshair(draw_target, cvec)


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
