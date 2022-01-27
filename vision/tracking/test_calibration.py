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

####################################################    MODE SET #######################################
camera_mode = CameraMode.IMAGE
function_mode = FunctionMode.HOMOGRAPHY_SHOW
########################################################################################################


if camera_mode == CameraMode.SINGLE_FISHEYE or camera_mode == CameraMode.DUAL_FISHEYE:
    fisheye_1 = dreadbot_fisheye.Fisheye(1, 1)
if camera_mode == CameraMode.DUAL_FISHEYE:
    fisheye_0 = dreadbot_fisheye.Fisheye(3, 0)
if camera_mode == CameraMode.IMAGE:
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
    undistorted_img_0 = fisheye_0.ret_undist()
    undistorted_img_1 = fisheye_1.ret_undist()

    cat_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)

    cv2.imshow('frame', cat_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break



'''
                  TEST HOMOGRAPHY
'''
while function_mode == FunctionMode.HOMOGRAPHY_SHOW:
    if camera_mode == CameraMode.IMAGE:
        undistorted_img_0 = cv2.imread('stitch_backup/undistorted_img0.png')
        undistorted_img_1 = cv2.imread('stitch_backup/undistorted_img1.png')
    else:
        undistorted_img_0 = fisheye_0.ret_undist()
        undistorted_img_1 = fisheye_1.ret_undist()

    kp0, dps0 = dreadbot_fisheye.detect(undistorted_img_0)
    kp1, dps1 = dreadbot_fisheye.detect(undistorted_img_1)

    cv2.drawKeypoints(undistorted_img_0, kp0, undistorted_img_0)
    cv2.drawKeypoints(undistorted_img_1, kp1, undistorted_img_1)

    homography_comparison = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)

    cv2.imshow('frame', homography_comparison)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

'''
                  TAKE IMAGE
'''
while function_mode == FunctionMode.TAKE_IMG:
    cv2.imwrite("stitch_backup/undistorted_img0.png", fisheye_0.ret_undist())
    cv2.imwrite("stitch_backup/undistorted_img1.png", fisheye_1.ret_undist())
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