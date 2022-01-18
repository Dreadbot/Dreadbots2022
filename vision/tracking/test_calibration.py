import cv2
import numpy as np

cap = cv2.VideoCapture(1)

test_point = (50, 50)

DIM=(640, 480)
K=np.array([[231.12849686105233, 0.0, 324.3670902176044], [0.0, 231.26214350748688, 268.85024970872524], [0.0, 0.0, 1.0]])
D=np.array([[-0.04246758993225627], [0.0012586698587831295], [-0.004301518520494384], [0.0008646618193537325]])  

map1, map2 = cv2.fisheye.initUndistortRectifyMap(K, D, np.eye(3), K, DIM, cv2.CV_16SC2)

rvec = np.array([[[0., 0., 0.]]])
tvec = np.array([[[0., 0., 0.]]])

# objp = np.array([[[(1595-new_K[0, 2])/new_K[0, 0], (922-new_K[1, 2])/new_K[1, 1], 0.]]])

focal_x = K[0,0]
focal_y = K[1,1]

offset_x = K[0, 2]
offset_y = K[1, 2]

test_objp = np.array([[[(test_point[0] - offset_x)/focal_x, (test_point[1] - offset_y)/focal_y, 0]]])

while True:
    ret, img = cap.read()

    undistorted_img = cv2.remap(img, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    cv2.circle(undistorted_img, test_point, 3, (0,0,255))

    rev_pts, rev_jac = cv2.fisheye.projectPoints(test_objp, rvec, tvec, K, D)
    rev_euc = (int(rev_pts[0, 0, 0]), int(rev_pts[0, 0, 1]))

    cv2.circle(img, rev_euc, 3, (0, 0, 255))

    cv2.imshow('fix', undistorted_img)
    cv2.imshow('raw', img)



    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
