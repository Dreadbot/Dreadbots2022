import cv2
import numpy as np

img = cv2.imread('output.png')

# colorSpace = cv2.COLOR_BGR2HLS

blurK = 3
lower = (150,150,150)
upper = (255,255,255)

eIts = 0
dIts = 3

# hsv = cv2.cvtColor(img, colorSpace)
    
blurred = cv2.blur(img, (blurK, blurK))
inRange = cv2.inRange(blurred, lower, upper)
erode = cv2.erode(inRange, None, iterations=eIts)
dilate = cv2.dilate(erode, None, iterations=dIts)

dilate = cv2.cvtColor(dilate, cv2.COLOR_GRAY2BGR)

compare = cv2.addWeighted(dilate, 0.8, img, 0.2, 1.0)

cv2.imshow('frame', compare)
cv2.waitKey(0)