import cv2
import numpy as np
import random
import dreadbot_fisheye

DIM = [480,640,3]

img = np.zeros(DIM, dtype=np.uint8)

p1_x, p1_y, p1_x2, p1_y2 = (50,50,100,100)

p2_x, p2_y, p2_x2, p2_y2 = (75,75,125,125)

gray = np.zeros([50,50,3], dtype=np.uint8)
# gray[::] = [180,56,101]
h, w, d = gray.shape
color_step = 255/w


light_gray = np.zeros([50,50,3], dtype=np.uint8)
# light_gray[::] = [201,160,91]
r = 0
g = 0
for col in range(w):
    r += color_step
    print(r)
    g += color_step
    gray[col:col+1, 0:h] = [50,g,100]
    light_gray[col:col+1, 0:h] = [r,50,100]


overlap_x1, overlap_y1, overlap_x2, overlap_y2 = (75,75,100,100)

img[p1_y:p1_y2, p1_x:p1_x2] = gray
img[p2_y:p2_y2, p2_x:p2_x2] = light_gray

ol_gray = gray[25:50, 25:50]
ol_lgray = light_gray[0:25, 0:25]

ol_width = 25
regions = 5
region_width = ol_width//regions
# print("Region Width: {0}".format(region_width))

a = 1
b = 0

weight_steps = 1/regions

# print(weight_steps)

for r in range(regions):
    gray_overlap = gray[0:25, r*region_width:(r+1)*region_width]
    light_gray_overlap = gray[0:25, r*region_width:(r+1)*region_width]

    img_slice = img[overlap_y1:overlap_y2, overlap_x1+(region_width*r):overlap_x1+(region_width*(r+1))]

    # print("Gray Shape:{0}\nLight Gray Shape:{1}\nRegion Shape:{2}".format(gray_overlap.shape, light_gray_overlap.shape, img_slice.shape))


    a -= weight_steps
    b += weight_steps

    a = round(a, 1)
    b = round(b, 1)

    gray_weighted = np.multiply(gray_overlap, a)
    light_gray_weighted = np.multiply(light_gray_overlap, b)

    print(gray_weighted[0:1, 0:1])
    print(light_gray_weighted[0:1, 0:1])



    img[overlap_y1:overlap_y2, overlap_x1+(region_width*r):overlap_x1+(region_width*(r+1))] = np.multiply(gray_overlap, a) + np.multiply(light_gray_overlap, b)
    # img[overlap_y1:overlap_y2, overlap_x1+(region_width*r):overlap_x1+(region_width*(r+1))] = [0,0,10*r]
    # print(img[overlap_y1:overlap_y1+1, overlap_x1+(region_width*r):overlap_x1+(region_width*r)+1])

cv2.imshow('frame', img)

cv2.waitKey(0)