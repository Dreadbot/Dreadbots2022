import cv2
import numpy as np
import dreadbot_fisheye

#True for camera, False for image
src = True

if src:
    cap1 = cv2.VideoCapture(3)
    cap2 = cv2.VideoCapture(1)
else:
    img = cv2.imread('0.png')

f_0_cam = dreadbot_fisheye.Fisheye(0)
f_1_cam = dreadbot_fisheye.Fisheye(1)

scale = 3
DIM = list(f_0_cam.DIM)
DIM_flip = DIM[::-1]

dim1 = DIM
dim2 = DIM
dim3 = DIM

# K = f_0_cam.K
# # D = f_0_cam.D

# K_scaled = K * dim1[0] / DIM[0]
# K_scaled[2,2] = 1.0

# assert dim1[0]/dim1[1] == DIM[0]/DIM[1], "Image to undistort needs to have same aspect ratio as the ones used in calibration"

# fish_K = cv2.fisheye.estimateNewCameraMatrixForUndistortRectify(K_scaled, D, dim2, np.eye(3))
# optimal_K = cv2.getOptimalNewCameraMatrix(K, D, dim2, 0.0)[0]



map1_0, map2_0 = cv2.fisheye.initUndistortRectifyMap(f_0_cam.K, f_0_cam.D, np.eye(3), f_0_cam.K, f_0_cam.DIM, cv2.CV_16SC2)
map1_1, map2_1 = cv2.fisheye.initUndistortRectifyMap(f_1_cam.K, f_1_cam.D, np.eye(3), f_1_cam.K, f_1_cam.DIM, cv2.CV_16SC2)
# print("Original K: {0}".format(K))
# print("Scaled K: {0}".format(K_scaled))
# print("Estimated K: {0}".format(new_K))
'''
Step 1 - Scale K by target/original   Make sure to reset K[2, 2] to 1 as it should always be 1
Step 2 - run an estimageNewK on newly scaled, inputting balance
'''


# bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)


while True:
    if src:
        ret, img0 = cap1.read()
        ret, img1 = cap2.read()


    
    undistorted_img_0 = cv2.remap(img0, map1_0, map2_0, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)
    undistorted_img_1 = cv2.remap(img1, map1_1, map2_1, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    undistorted_img_0 = cv2.rotate(undistorted_img_0, cv2.ROTATE_90_CLOCKWISE)
    undistorted_img_1 = cv2.rotate(undistorted_img_1, cv2.ROTATE_90_COUNTERCLOCKWISE)

    # undist_0_computed = detect(undistorted_img_0)
    # undist_1_computed = detect(undistorted_img_1)





    # cat_img = np.concatenate((undistorted_img_0, undistorted_img_1), axis=1)
    # cv2.imshow('frame', cat_img)
    # cv2.imwrite("stitch_backup/cat_img.png", cat_img)
    cv2.imwrite("stitch_backup/undistorted_img0.png", undistorted_img_0)
    cv2.imwrite("stitch_backup/undistorted_img1.png", undistorted_img_1)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

    break

if src:
    cap1.release()
    cap2.release()
cv2.destroyAllWindows()
