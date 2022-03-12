import cv2
import numpy as np

cap = cv2.VideoCapture(0)

while True:
    try:
        dist = int(input('dist: '))
    except:
        break

    ret, frame = cap.read()
    cv2.imwrite(f"focal/{dist}.png", frame)

cap.release()
