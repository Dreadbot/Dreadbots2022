from pickle import GLOBAL
import cv2
from cv2 import MIXED_CLONE
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
    fisheye_0 = dreadbot_fisheye.Fisheye(1, 0, adj_exposure=0.01)
    
if camera_mode == CameraMode.DUAL_FISHEYE:
    print("Fisheye ID 2 Enabled")
    fisheye_1 = dreadbot_fisheye.Fisheye(2, 1, adj_exposure=0.01)
    
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
    undistorted_img_0 = fisheye_0.ret_undist()
    undistorted_img_1 = fisheye_0.ret_dist()

    p_undist = (250,320)I
    p_x, p_y = p_undist

    cv2.circle(undistorted_img_0, p_undist, 5, (0,0,255), thickness=-1)

    fixed_pts = fisheye_0.reverse_project_point(p_x, p_y)

    cv2.circle(undistorted_img_1, fixed_pts, 5, (0,0,255), thickness=-1)

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
        undistorted_img_1 = fisheye_0.ret_undist()
        undistorted_img_0 = fisheye_1.ret_undist()

    mixed_img = dreadbot_fisheye.image_blend(undistorted_img_0, undistorted_img_1, 50)
    

    cv2.imshow('frame', mixed_img)
    
    # cv2.waitKey(0)
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