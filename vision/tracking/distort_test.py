import cv2
import numpy as np

square_D = 0.001

D = np.array([
    [square_D],
    [square_D],
    [square_D],
    [square_D]
])

K = np.array([
    [100.0, 0.0, 50.0],
    [0.0, 100.0, 50.0],
    [0.0, 0.0, 1.0]
])

DIM = [100,100]
dim1 = DIM
dim2 = DIM
dim3 = DIM

scaled_K = K * dim1[0] / DIM[0]
scaled_K[2,2] = 1.0

# 	cv.getOptimalNewCameraMatrix(	cameraMatrix, distCoeffs, imageSize, alpha[, newImgSize[, centerPrincipalPoint]]	) 
optimal_K = cv2.getOptimalNewCameraMatrix(K, D, DIM, 1.0)
fish_K = cv2.fisheye.estimateNewCameraMatrixForUndistortRectify(scaled_K, D, dim2, np.eye(3))

print(optimal_K)
print('-----')
print(fish_K)
