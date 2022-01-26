import cv2
import numpy as np
import dreadbot_fisheye

fisheye = dreadbot_fisheye.Fisheye(2, 0)

while True:
    cv2.imshow('frame', fisheye.ret_undist())

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

fisheye.unload()
cv2.destroyAllWindows()