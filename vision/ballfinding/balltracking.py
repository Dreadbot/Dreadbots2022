# import the necessary packages
import numpy as np
import cv2
import imutils
import math

# construct the argument parse and parse the arguments
# define the lower and upper boundaries of the "green"
# ball in the HSV color space, then initialize the
# list of tracked points

# blueLower = (92, 10, 2)
# blueUpper = (118, 241, 255)

blueLower = (0, 31, 219)
blueUpper = (26, 255, 255)
# if a video path was not supplied, grab the reference
# to the webcam
vs = cv2.VideoCapture(1)
# allow the camera or video file to warm up
# keep looping
while True:
    # grab the current frame
    ret, frame = vs.read()
    if not ret:
        break
    # resize the frame, blur it, and convert it to the HSV
    # color space
    frame = imutils.resize(frame, width=600)
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(blurred, cv2.COLOR_BGR2HSV)
    # construct a mask for the color "green", then perform
    # a series of dilations and erosions to remove any small
    # blobs left in the mask
    mask = cv2.inRange(hsv, blueLower, blueUpper)
    mask = cv2.erode(mask, None, iterations=14)
    mask = cv2.dilate(mask, None, iterations=14)

    cv2.imshow("Mask", mask)

    # find contours in the mask and initialize the current
    # (x, y) center of the ball
    cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
                            cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    center = None
    # only proceed if at least one contour was found
    if len(cnts) > 0:
        # find the largest contour in the mask, then use
        # it to compute the minimum enclosing circle and
        # centroid

        image = frame.copy()
        cv2.drawContours(image=image, contours=cnts, contourIdx=-1,
                         color=(0, 255, 0), thickness=2, lineType=cv2.LINE_AA)
        cv2.imshow("Cnts", image)

        # confirmedBalls = []

        # for c in cnts:
        #     dists = []
        #     M = cv2.moments(c)
        #     center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
        #     for point in c:
        #         distX = point[0][0] - center[0]
        #         distY = point[0][1] - center[1]
        #         dist = abs(math.sqrt(pow(distX, 2) + pow(distY, 2)))
        #         dists.append(dist)

        #     a = 0
        #     for d in dists:
        #         a += d

        #     avg = a / len(dists)

        #     ball = True
        #     for d in dists:
        #         if not (d > avg - 10 and d < avg + 10):
        #             ball = False
        #             break

        #     if ball:
        #         confirmedBalls.append(c)

        # if len(confirmedBalls) == 0:
        #     continue

        c = max(cnts, key=cv2.contourArea)

        ((x, y), radius) = cv2.minEnclosingCircle(c)
        M = cv2.moments(c)
        center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
        # print(center)

        # only proceed if the radius meets a minimum size
        if radius > 10:
            # draw the circle and centroid on the frame,
            # then update the list of tracked points
            cv2.circle(frame, (int(x), int(y)), int(radius),
                       (0, 255, 255), 2)
            cv2.circle(frame, center, 5, (0, 0, 255), -1)
    cv2.imshow("Frame", frame)
    key = cv2.waitKey(1) & 0xFF
    if key == ord("q"):
        break

vs.release()
cv2.destroyAllWindows()
