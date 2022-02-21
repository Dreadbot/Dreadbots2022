import numpy as np
import cv2

cap = cv2.VideoCapture(2)

cx, cy = (320, 240)

img_points = np.array([
    [320, 240],
    [75, 51],
    [157, 55],
    [271, 65],
    [374, 73],
    [439, 78],
    [507, 83]
])

K = np.array([ # INTRINSIC
    [667.8641230884809, 0.0, 336.8816006655839],
    [0.0, 669.2419216918619, 236.21359451522662],
    [0.0, 0.0, 1.0]
])

# Global Z - 4.25in

'''Point Z/d* vals

15.125
15.13
15.25
16.125
16.75
17.75

obj_points initialized with d*, Z to be calculated
'''

X_center, Y_center, Z_center = (0,0,0) #Treating camera as origin
obj_points = np.array([
    [0, 0, 0],
    [-4.5, 15.125, 4.25],
    [-3.5, 15.13, 4.25],
    [-1.5, 15.25, 4.25],
    [2, 16.125, 4.25],
    [2.55, 16.75, 4.25],
    [4.25, 17.75, 4.25]
])
# LEAVING OFF WITH SOME PROB RELATED TO POSSIBLY NEEDING INV OF INTRINSIC
# for i in range(len(obj_points)):
#     wX, wY, wd = obj_points[i]

#     d1 = np.sqrt(np.square(wX) + np.square(wY))
#     wZ = np.sqrt(np.square(wd) - np.square(d1))
#     print(wZ)

#     obj_points[i, 2] = wZ


ret, rvec, tvec = cv2.solvePnP(obj_points, img_points, K, np.zeros((4,1)), flags=0)

while True:
    ret, img = cap.read()

    
    # cv2.setMouseCallback('frame', print_point)
    for (x,y) in img_points:
        cv2.circle(img, (x, y), 3, (0,0,255), thickness=-1)

    cv2.imshow('frame', img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
