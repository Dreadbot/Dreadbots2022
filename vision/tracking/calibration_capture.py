import cv2
import numpy as np
import os

cap = cv2.VideoCapture(2)

file_cnt = 0
loop_cnt = 0

capturing = False


while True:
    if loop_cnt == 1000:
        loop_cnt = 0

    loop_cnt += 1

    ret, img = cap.read()

    filename = "./checkboard_imgs_fcam0/{0}.png".format(file_cnt)

    os.system('cls')
    print("Captured: {0}".format(file_cnt))



    if capturing and loop_cnt % 50 == 0:
        cv2.imwrite(filename, img)
        file_cnt += 1


    cv2.imshow('Frame', img)
    key = cv2.waitKey(1)
    if key & 0xFF == ord('q'):
        break
    elif key & 0xFF == ord('p'):
        capturing = not capturing

        


cap.release()
cv2.destroyAllWindows()