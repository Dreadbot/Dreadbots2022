import cv2
import numpy as np

cap = cv2.VideoCapture(2)


DIM=(640, 480)
K=np.array([[231.12849686105233, 0.0, 324.3670902176044], [0.0, 231.26214350748688, 268.85024970872524], [0.0, 0.0, 1.0]])
D=np.array([[-0.04246758993225627], [0.0012586698587831295], [-0.004301518520494384], [0.0008646618193537325]])  

map1, map2 = cv2.fisheye.initUndistortRectifyMap(K, D, np.eye(3), K, DIM, cv2.CV_16SC2)

while True:
    ret, img = cap.read()

    undistorted_img = cv2.remap(img, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    cv2.imshow('hehe', undistorted_img)

    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
