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
function_mode = FunctionMode.HOMOGRAPHY_SHOW
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
    undistorted_img_1 = fisheye_1.ret_undist()

    cat_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)

    cv2.imshow('frame', cat_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

'''
                  TEST HOMOGRAPHY
'''

if function_mode == FunctionMode.HOMOGRAPHY_SHOW: 
    x_high_offset_0 = 0 #Do not reset
    x_low_offset_0 = -35

    x_high_offset_1 = 23 #Do not reset
    x_low_offset_1 = 38

    left_overlap_top = (GLOBAL_X - (GLOBAL_X//9) + x_high_offset_0, 0)
    left_overlap_bottom = (GLOBAL_X - (GLOBAL_X//9) + x_low_offset_0, GLOBAL_Y)

    left_overlap_avg_x = (left_overlap_top[0] + left_overlap_bottom[0]) // 2

    left_overlap_straight_top = (left_overlap_avg_x, 0)
    left_overlap_straight_bottom = (left_overlap_avg_x, GLOBAL_Y)

    left_overlap_width = GLOBAL_X - left_overlap_avg_x

    right_overlap_top = (GLOBAL_X//8 + x_high_offset_1, 0)
    right_overlap_bottom = (GLOBAL_X//8 + x_low_offset_1, GLOBAL_Y)

    right_overlap_avg_x = (right_overlap_top[0] + right_overlap_bottom[0]) // 2

    right_overlap_straight_top = (right_overlap_avg_x, 0)
    right_overlap_straight_bottom = (right_overlap_avg_x, GLOBAL_Y)

    right_overlap_width = right_overlap_avg_x

    overlaps = [left_overlap_width, right_overlap_width]

    # max_overlap_width = max(overlaps)
    max_overlap_width = 75



    # mixed_img_w = (GLOBAL_X-max_overlap_width) + max_overlap_width + (GLOBAL_X-max_overlap_width)
    mixed_img_w = 2* GLOBAL_X - max_overlap_width
    mixed_img_h = GLOBAL_Y

    transparency_step = 1/max_overlap_width

    mixed_img = np.zeros([mixed_img_h, mixed_img_w, 3], dtype=np.uint8)
    # mixed_img.fill(255)

while function_mode == FunctionMode.HOMOGRAPHY_SHOW:
    if camera_mode == CameraMode.IMAGE:
        undistorted_img_0 = cv2.imread('stitch_backup/undistorted_img0.png')
        undistorted_img_1 = cv2.imread('stitch_backup/undistorted_img1.png')
    else:
        undistorted_img_1 = fisheye_0.ret_undist()
        undistorted_img_0 = fisheye_1.ret_undist()

    # undistorted_img_0 = cv2.cvtColor(undistorted_img_0, cv2.COLOR_BGR2BGRA)
    # undistorted_img_1 = cv2.cvtColor(undistorted_img_1, cv2.COLOR_BGR2BGRA)

    # mixed_img = cv2.cvtColor(mixed_img, cv2.COLOR_BGR2BGRA)


    mixed_img[0:GLOBAL_Y, 0:GLOBAL_X-max_overlap_width] = undistorted_img_0[0:GLOBAL_Y, 0:GLOBAL_X-max_overlap_width]
    mixed_img[0:GLOBAL_Y, GLOBAL_X:mixed_img_w] = undistorted_img_1[0:GLOBAL_Y, max_overlap_width:GLOBAL_X]

    
    left_input = undistorted_img_0[0:GLOBAL_Y, GLOBAL_X-max_overlap_width:GLOBAL_X]
    right_input = undistorted_img_1[0:GLOBAL_Y, 0:max_overlap_width]

    # overlapped = undistorted_img_0[0:GLOBAL_Y, GLOBAL_X-max_overlap_width:GLOBAL_X] + undistorted_img_1[0:GLOBAL_Y, 0:max_overlap_width]
    final_overlap = np.zeros([GLOBAL_Y, max_overlap_width, 3], dtype=np.uint8)

    cur_transparency = 1

    for col in range(max_overlap_width):
        left_strip = left_input[0:GLOBAL_Y, col:col+1]
        right_strip = right_input[0:GLOBAL_Y, col:col+1]

        if not left_strip.any():
            left_strip = np.zeros([GLOBAL_Y, 1, 3])

        if not right_strip.any():
            right_strip = np.zeros([GLOBAL_Y, 1, 3])

        final_overlap[0:GLOBAL_Y, col:col+1] = cv2.addWeighted(left_strip, cur_transparency, right_strip, 1-cur_transparency, 0)

        cur_transparency -= transparency_step


    # final_overlap[::] = [255,0,0]
    # print(final_overlap.shape)
    mixed_img[0:GLOBAL_Y, GLOBAL_X-(max_overlap_width):GLOBAL_X] = final_overlap

    


    '''
    WHERE I LEFT OFF:
    Sliced in left image into mixed image, need to stitch in right with respect to overlap and then insert overlap
    Think about handling for overlap zones that aren't even
    '''

    # full_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)

    cv2.imshow('frame', mixed_img)
    # cv2.imshow('frame', undistorted_img_0)
    
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