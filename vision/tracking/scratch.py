import cv2
import numpy as np
import random
import dreadbot_fisheye
import math

DIM = [480,640,3]

right = np.zeros(DIM, dtype=np.uint8)
left = np.zeros(DIM, dtype=np.uint8)

right[::] = [100,33,120]
left[::] = [93,120,25]

ol_width = []

gap = 30

for row in range(480):
    ol_width.append(int(abs(math.sin(row*0.025)*70) + gap))



# output = dreadbot_fisheye.image_blend(left, right, 100)

final_width = 2*640 - max(ol_width)

output = np.zeros([480, final_width, 3], dtype=np.uint8)

_, cx_output, _ = output.shape

cx_output *= 0.5

cx_output = int(cx_output)

for i in range(len(ol_width)):
    span = ol_width[i]

    left_strip = left[i:i+1, 640-span:640]
    right_strip = right[i:i+1, 0:span]

    transparency_step = 1/(2*span)

    left_weight = 1
    right_weight = 0

    output_strip = np.zeros([1, 2*span, 3], dtype=np.uint8)

    strip_width = 2*span

    for col in range(span*2):
        left_piece = left_strip[0:1, span-col:span-col+1]
        right_piece = right_strip[0:1, col:col+1]
        # print(left_piece.shape)
        print(right_piece.shape)
        # if left_piece.any() is None:
        #     left_piece = np.zeros([1, span, 3], dtype=np.uint8)
        # if right_piece.any() is None:
        #     right_piece = np.zeros([1, span, 3], dtype=np.uint8)
        # output_strip[i:i+1, strip_width+col:strip_width+col+1] = cv2.addWeighted(left_piece, left_weight, right_piece, right_weight, 0)
        output_strip[i:i+1, strip_width+col:strip_width+col+1] = [255,255,255]
        left_weight -= transparency_step
        right_weight += transparency_step
    output[i:i+1, cx_output-span:cx_output+span] = output_strip


cv2.imshow('frame',output)
cv2.waitKey(0)
