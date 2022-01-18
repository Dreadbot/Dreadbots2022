import cv2
import imutils
import numpy as np

lowerV = (0, 31, 219)
upperV = (26, 255, 255)

camera = cv2.VideoCapture(1)

while True:
    ret, frame = camera.read()

    if not ret:
        break

    frame = imutils.resize(frame, width=600)
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(blurred, cv2.COLOR_BGR2HSV)

    mask = cv2.inRange(hsv, lowerV, upperV)
    mask = cv2.erode(mask, None, iterations=14)
    mask = cv2.dilate(mask, None, iterations=14)

    blobParams = cv2.SimpleBlobDetector_Params()
    # blobParams.filterByCircularity = True
    # blobParams.minCircularity = 0.1
    blob = cv2.SimpleBlobDetector_create(blobParams)

    keys = blob.detect(mask)
    image = cv2.drawKeypoints(mask, keys, np.array([]), (0,0,255), cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)

    cv2.imshow("Blobs", image)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

camera.release()
cv2.destroyAllWindows()
