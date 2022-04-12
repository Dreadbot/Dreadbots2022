from cscore import CameraServer
import cv2

path = 'mask.png'

first_read = cv2.imread(path)

cs = CameraServer.getInstance()
outputFrame = cs.putVideo("Mask", first_read.shape[1], first_read.shape[0])

try:
    while True:
        img = cv2.imread(path)
        outputFrame.putFrame(img)
except KeyboardInterrupt:
    pass

