import cv2
import numpy as np


trainImg = cv2.cvtColor(cv2.imread("stitch_backup/undistorted_img0.png"), cv2.COLOR_BGR2GRAY)
queryImg = cv2.cvtColor(cv2.imread("stitch_backup/undistorted_img1.png"), cv2.COLOR_BGR2GRAY)

def detect(img):
    descriptor = cv2.ORB_create()

    (key_pts, features) = descriptor.detectAndCompute(img, None)
    return (key_pts, features)


kp0, des0 = detect(trainImg)
kp1, des1 = detect(queryImg)

FLANN_INDEX = 1

index_params = dict(algorithm=FLANN_INDEX, trees=5)
search_params = dict(checks=50)

flann = cv2.FlannBasedMatcher(index_params, search_params)

des0 = np.float32(des0)
des1 = np.float32(des1)

matches = flann.knnMatch(des0, des1, k=2)

matches_mask = [[0,0] for i in range(len(matches))]
good_matches = []

for i, (m,n) in enumerate(matches):
    if m.distance < 0.685*n.distance:
        matches_mask[i] = [1,0]
        good_matches.append(m)


src_pts = np.float32([kp0[i] for (_, i) in matches])
dst_pts = np.float32([kp1[i] for (i, _) in matches])

M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC, 5.0)

w = trainImg[1] + queryImg.shape[1]
h = trainImg[0] + queryImg.shape[0]

print(type(w))
print(type(h))
res = cv2.warpPerspective(trainImg, M, (w,h))
result[0:queryImg.shape[0], 0:queryImg.shape[1]] = queryImg

cv2.imshow('frame', res)
cv2.waitKey(0)

# draw_params = dict(matchColor = (0,255,0),
#                    singlePointColor = (255,0,0),
#                    matchesMask = m,
#                    flags = 0)

# img3 = cv2.drawMatchesKnn(trainImg,kp0,queryImg,kp1,matches,None,**draw_params)

# cv2.imshow('frame', img3)
# cv2.waitKey(0)