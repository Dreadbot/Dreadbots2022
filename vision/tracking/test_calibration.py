import cv2
import numpy as np
import dreadbot_fisheye
from enum import Enum


# True for camera, False for image
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
function_mode = FunctionMode.TAKE_IMG
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

    horizontal_y = 260

    vertical_x = 330

    horizontal_calibrated_point = (87, horizontal_y)
    horizontal_testing_point = (207, horizontal_y)

    vertical_calibrated_point = (vertical_x, 18)
    vertical_testing_point = (vertical_x, 118)

    v_c_x, v_c_y = vertical_calibrated_point
    v_t_x, v_t_y = vertical_testing_point

    h_c_x, h_c_y = horizontal_calibrated_point
    h_t_x, h_t_y = horizontal_testing_point

    cx, cy = (320, 240)

    h_f_c_x, h_f_c_y = fisheye_0.reverse_project_point(h_c_x, h_c_y)
    h_f_t_x, h_f_t_y = fisheye_0.reverse_project_point(h_t_x, h_t_y)

    v_f_c_x, v_f_c_y = fisheye_0.reverse_project_point(v_c_x, v_c_y)
    v_f_t_x, v_f_t_y = fisheye_0.reverse_project_point(v_t_x, v_t_y)


    h__f_cx, h__f_cy = fisheye_0.reverse_project_point(cx, cy)

    cv2.line(undistorted_img_0, (0, 240), (640, 240), (0, 0, 255))  # CX LINE
    cv2.line(undistorted_img_0, (320, 0), (320, 480), (0, 0, 255))  # CY LINE

    cv2.line(undistorted_img_0, (h_c_x, 0), (h_c_x, 480), (0, 255, 0))  # X Calibrated Line up
    cv2.line(undistorted_img_0, (h_t_x, 0), (h_t_x, 480), (0, 255, 0))  # X Testing Line up

    cv2.line(undistorted_img_0, (0, v_c_y), (640, v_c_y), (255, 0, 0))  # Y Calibrated Line Up
    cv2.line(undistorted_img_0, (0, v_t_y), (640, v_t_y), (255, 0, 0))  # Y Testing Line Up

    cv2.line(undistorted_img_0 ,(vertical_x, 0), (vertical_x, 480), (255,0,0)) # X Line Up
    cv2.line(undistorted_img_0, (0, h_c_y), (640, h_c_y), (0, 255, 0))  # Y Line Up

    cv2.circle(undistorted_img_0, horizontal_calibrated_point, 3, (0, 255, 0), thickness=-1)
    cv2.circle(undistorted_img_0, horizontal_testing_point, 3, (0, 255, 0), thickness=-1)

    cv2.circle(undistorted_img_0, vertical_calibrated_point, 3, (255,0,0), thickness=-1)
    cv2.circle(undistorted_img_0, vertical_testing_point, 3, (255,0,0), thickness=-1)

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

    mixed_img = dreadbot_fisheye.image_blend(
        undistorted_img_0, undistorted_img_1, 25, blend_mode=dreadbot_fisheye.LINEAR)

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
