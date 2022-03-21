from tkinter import W
import cv2
import numpy as np


img = cv2.imread('output.png')
img = cv2.cvtColor(img, cv2.COLOR_BGR2HLS)

l = (10,50,150)
u = (255,255,255)

mask = cv2.inRange(img, l, u)
erode = cv2.erode(mask, None, iterations=1)
# dilate = cv2.dilate(erode, None, iterations=3)


cv2.imshow('frame', erode)
cv2.waitKey()