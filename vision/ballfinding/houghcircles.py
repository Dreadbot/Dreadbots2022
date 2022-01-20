import cv2
import imutils
import util
import numpy as np

camera = cv2.VideoCapture(0)
rangeName = "blue"

while True:
    ret, frame = camera.read()

    if not ret:
        break

    if not util.isLiveRange(rangeName):
        util.updateLiveRange(rangeName, (0, 0, 0), (255, 255, 255))
    
    range = util.getLiveRange()
    lower = range["lower"]
    upper = range["upper"]

    mask = util.getMask(frame, lower, upper)
    circleFrame = frame.copy()

    circles = cv2.HoughCircles(mask, cv2.HOUGH_GRADIENT, 1, 20, param1=50, param2=30, minRadius=0, maxRadius=0)
    circles = np.uint16(np.around(circles))
    for c in circles:
        cv2.circle(circleFrame, (c[0],c[1]), c[2], (0, 255, 255), 2)

    cv2.imshow("Circles", circleFrame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

camera.release()
cv2.destroyAllWindows()