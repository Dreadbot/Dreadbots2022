import cv2
import numpy as np
import dreadbot_fisheye
from enum import Enum


#True for camera, False for image
src = True

class CameraMode(Enum):
    IMAGE = 0
    SINGLE_FISHEYE = 1
    DUAL_FISHEYE = 2


class FunctionMode(Enum):
    SINGLE_FISHEYE = 0
    CAT_FISHEYE = 1
    HOMOGRAPHY_SHOW = 2
    TAKE_IMG = 3

GLOBAL_X = 640
GLOBAL_Y = 480

####################################################    MODE SET #######################################
camera_mode = CameraMode.DUAL_FISHEYE
function_mode = FunctionMode.CAT_FISHEYE
########################################################################################################


if camera_mode == CameraMode.SINGLE_FISHEYE or camera_mode == CameraMode.DUAL_FISHEYE:
    print("Fisheye ID 1 Enabled")
    fisheye_0 = dreadbot_fisheye.Fisheye(1, 0)
    
if camera_mode == CameraMode.DUAL_FISHEYE:
    print("Fisheye ID 2 Enabled")
    fisheye_1 = dreadbot_fisheye.Fisheye(2, 1)
    
if camera_mode == CameraMode.IMAGE:
    print("Image loaded")
    img = cv2.imread('0.png')


'''
                  SINGLE FISHEYE
'''
while function_mode == FunctionMode.SINGLE_FISHEYE:
    undistorted_img = fisheye_0.ret_undist()

    cv2.imshow('frame', undistorted_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break


'''
                  CONCAT DUAL FISHEYE
'''
while function_mode == FunctionMode.CAT_FISHEYE:
    if camera_mode == CameraMode.DUAL_FISHEYE or camera_mode == CameraMode.SINGLE_FISHEYE:
        undistorted_img_0 = fisheye_0.ret_undist()
        undistorted_img_1 = fisheye_0.ret_dist()
    else:
        undistorted_img_0 = cv2.imread('undistorted.png')
        undistorted_img_1 = cv2.imread('distorted.png')

    calibrated_point = (87,255)
    testing_point = (207,255)

    c_x, c_y = calibrated_point
    t_x , t_y = testing_point

    _cx, _cy = (320,240)

    f_c_x, f_c_y = fisheye_0.reverse_project_point(c_x, c_y)
    f_t_x, f_t_y = fisheye_0.reverse_project_point(t_x, t_y)
    _f_cx, _f_cy = fisheye_0.reverse_project_point(_cx, _cy)
    print("Fixed Center: ({0}, {1})".format(_f_cx, _f_cy))
    print("Fixed Calibration: ({0}, {1})\nFixed Test: ({2}, {3})".format(f_c_x, f_c_y, f_t_x, f_t_y))
    
    print("Calculated Angle: {0} degrees".format(fisheye_0.calculate_angle(t_x, t_y)))
    break
    
    cv2.line(undistorted_img_0, (0,240), (640, 240), (0,0,255)) #CX LINE
    cv2.line(undistorted_img_0, (320,0), (320, 480), (0,0,255)) #CX LINE

    cv2.line(undistorted_img_0, (c_x, 0), (c_x,480), (0,255,0)) #X Calibrated Line up
    cv2.line(undistorted_img_0, (t_x, 0), (t_x,480), (0,255,0)) #X Calibrated Line up

    cv2.line(undistorted_img_0, (0, c_y), (640, c_y), (0,255,0)) #Y Line up

    cv2.circle(undistorted_img_0, calibrated_point, 3, (0,255,0), thickness=-1)
    cv2.circle(undistorted_img_0, testing_point, 3, (0,255,0), thickness=-1)


    cat_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)

    cv2.imshow('frame', cat_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

'''
                  TEST HOMOGRAPHY
'''

flip = True

while function_mode == FunctionMode.HOMOGRAPHY_SHOW:
    if flip:
        undistorted_img_0 = fisheye_0.ret_undist()
        undistorted_img_1 = fisheye_1.ret_undist()
    else:
        undistorted_img_1 = fisheye_0.ret_undist()
        undistorted_img_0 = fisheye_1.ret_undist()

    mixed_img = dreadbot_fisheye.image_blend(undistorted_img_0, undistorted_img_1, 25, blend_mode=dreadbot_fisheye.LINEAR)
    

    cv2.imshow('frame', mixed_img)
    
    # cv2.waitKey(0)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

'''
                  TAKE IMAGE
'''
while function_mode == FunctionMode.TAKE_IMG:
    cv2.imwrite("undistorted.png", fisheye_0.ret_undist())
    cv2.imwrite("distorted.png", fisheye_0.ret_dist())
    break



# while True:


#     # undist_0_kps, undist_0_dps = detect(cv2.cvtColor(undistorted_img_0, cv2.COLOR_BGR2GRAY))
#     # undist_1_kps, undist_1_dps = detect(cv2.cvtColor(undistorted_img_1, cv2.COLOR_BGR2GRAY))

#     # cv2.drawKeypoints(undistorted_img_0, undist_0_kps, undistorted_img_0)
#     # cv2.drawKeypoints(undistorted_img_1, undist_1_kps, undistorted_img_1)

#     # cv2.imshow('frame', undistorted_img_0)

#     # cat_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)
#     # cv2.imshow('frame', cat_img)
#     # cv2.imwrite("stitch_backup/cat_img.png", cat_img)
#     cv2.imwrite("stitch_backup/undistorted_img0.png", undistorted_img_0)
#     cv2.imwrite("stitch_backup/undistorted_img1.png", undistorted_img_1)

#     key = cv2.waitKey(1)
#     if key & 0xFF == ord('q'):
#         break

#     break

# if src:##UPDATE EXITING
#     cap1.release()
#     cap2.release()
# cv2.destroyAllWindows()

if camera_mode == CameraMode.SINGLE_FISHEYE or camera_mode == CameraMode.DUAL_FISHEYE:
    fisheye_0.unload()

if camera_mode == CameraMode.DUAL_FISHEYE:
    fisheye_1.unload()

cv2.destroyAllWindows()