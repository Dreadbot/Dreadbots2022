import numpy as np
import cv2
import imutils
import math

blueLower = (49, 0, 0)
blueUpper = (134, 255, 255)

vs = cv2.VideoCapture(1)

while True:
    ret, frame = vs.read()

    if not ret:
        break

    frame = imutils.resize(frame, width=600)
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(blurred, cv2.COLOR_BGR2HLS)

    mask = cv2.inRange(hsv, blueLower, blueUpper)
    mask = cv2.erode(mask, None, iterations=14)
    mask = cv2.dilate(mask, None, iterations=14)

    cv2.imshow("Mask", mask)

    cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
                            cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    center = None

    if len(cnts) > 0:
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
        
        areas = {}
        for con in cnts:
            ((x, y), radius) = cv2.minEnclosingCircle(con)
            cntArea = cv2.contourArea(con)
            if cntArea == 0: continue
            circleArea = math.pi * radius**2 # r**2 does the same thing lmao
            # print(circleArea)
            p = cntArea / circleArea
            print(p)
            areas[p] = con
            # print(areas[p])


        greatestArea = max(areas.keys())
        # print(areas)
        # print(int(greatestArea))
        c = areas[greatestArea]

        ((x, y), radius) = cv2.minEnclosingCircle(c)
        M = cv2.moments(c)
        center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
        # print(center)

        cv2.circle(frame, (int(x), int(y)), int(radius),
                    (0, 255, 255), 2)
        cv2.circle(frame, center, 5, (0, 0, 255), -1)
    cv2.imshow("Frame", frame)
    key = cv2.waitKey(1) & 0xFF
    if key == ord("q"):
        break

vs.release()
cv2.destroyAllWindows()
