import cv2
import numpy as np
import dreadbot_fisheye

#True for camera, False for image
src = False

if src:
    cap = cv2.VideoCapture(1)
else:
    img = cv2.imread('0.png')

f_0_cam = dreadbot_fisheye.Fisheye(0)

scale = 3
DIM = list(f_0_cam.DIM)
DIM_flip = DIM[::-1]

dim1 = DIM
dim2 = DIM
dim3 = DIM

K = f_0_cam.K
D = f_0_cam.D

K_scaled = K * dim1[0] / DIM[0]
K_scaled[2,2] = 1.0

assert dim1[0]/dim1[1] == DIM[0]/DIM[1], "Image to undistort needs to have same aspect ratio as the ones used in calibration"

fish_K = cv2.fisheye.estimateNewCameraMatrixForUndistortRectify(K_scaled, D, dim2, np.eye(3))
optimal_K = cv2.getOptimalNewCameraMatrix(K, D, dim2, 0.0)[0]


map1, map2 = cv2.fisheye.initUndistortRectifyMap(K, D, np.eye(3), optimal_K, dim3, cv2.CV_16SC2)

# print("Original K: {0}".format(K))
# print("Scaled K: {0}".format(K_scaled))
# print("Estimated K: {0}".format(new_K))
'''
Step 1 - Scale K by target/original   Make sure to reset K[2, 2] to 1 as it should always be 1
Step 2 - run an estimageNewK on newly scaled, inputting balance
'''


while True:
    if src:
        ret, img = cap.read()
    
    undistorted_img = cv2.remap(img, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    cv2.imshow('fix', undistorted_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

if src:
    cap.release()
cv2.destroyAllWindows()
