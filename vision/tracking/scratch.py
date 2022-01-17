import numpy as np
import cv2

cam_id = 0
try:


while True:
    ret, img = cap.read()
    
    print(img)

    cv2.imshow('Frame', img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cv2.destroyAllWindows()
cap.release()
