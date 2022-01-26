import cv2
import numpy as np
import sklearn

img0 = cv2.cvtColor(cv2.imread("stitch_backup/undistorted_img0.png"), cv2.COLOR_BGR2GRAY)
img1 = cv2.cvtColor(cv2.imread("stitch_backup/undistorted_img1.png"), cv2.COLOR_BGR2GRAY)

def detect(img):
    descriptor = cv2.ORB_create()

    (key_pts, features) = descriptor.detectAndCompute(img, None)
    return (key_pts, features)


kp0, des0 = detect(img0)
kp1, des1 = detect(img1)

FLANN_INDEX = 0

index_params = dict(algorithm=FLANN_INDEX, trees=5)
search_params = dict(checks=1)

flann = cv2.FlannBasedMatcher(index_params, search_params)

des0 = np.float32(des0)
des1 = np.float32(des1)

matches = flann.knnMatch(des0, des1, k=2)

matches_mask = [[0,0] for i in range(len(matches))]

for i, (m,n) in enumerate(matches):
    if m.distance < 0.7*n.distance:
        matches_mask[i] = [1,0]

draw_params = dict(matchColor = (0,255,0),
                   singlePointColor = (255,0,0),
                   matchesMask = matches_mask,
                   flags = 0)

img3 = cv2.drawMatchesKnn(img0,kp0,img1,kp1,matches,None,**draw_params)

cv2.imshow('frame', img3)
cv2.waitKey(0)