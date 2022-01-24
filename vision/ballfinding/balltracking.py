import numpy as np
import cv2
import imutils
import math
import util


def main():
    rangeName = input("Range: ")

    if not util.isLiveRange(rangeName):
        # print("NOT RANGE")
        util.updateLiveRange(rangeName, (0, 0, 0), (255, 255, 255))

    range = util.getLiveRange(rangeName)
    manip = util.getManipulation()

    vs = cv2.VideoCapture(0)
    vs.set(cv2.CAP_PROP_EXPOSURE, -4)
    util.setupSliderWindow(
        "hsv", "Trackbars", range["lower"], range["upper"], manip["erode"], manip["dilate"], manip["blur"])

    while True:
        ret, frame = vs.read()

        if not ret:
            break

        hL, sL, vL, hU, sU, vU, erode, dilate, blur = util.getSliderValues(
            "hsv", "Trackbars")

        lower = (hL, sL, vL)
        upper = (hU, sU, vU)

        mask = util.getMask(frame, lower, upper, erode, dilate, blur)

        cv2.imshow("Mask", mask)

        cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)
        center = None

        frame = imutils.resize(frame, width=600)

        if len(cnts) > 0:
            image = frame.copy()
            cv2.drawContours(image=image, contours=cnts, contourIdx=-1,
                             color=(0, 255, 0), thickness=2, lineType=cv2.LINE_AA)
            cv2.imshow("Cnts", image)

            circles = frame.copy()

            areas = {}
            for con in cnts:
                ((x, y), radius) = cv2.minEnclosingCircle(con)
                cv2.circle(circles, (int(x), int(y)), int(radius),
                           (0, 0, 0), 2)
                cntArea = cv2.contourArea(con)
                # if cntArea == 0 or radius <= 40: continue
                circleArea = math.pi * radius**2  # r**2 does the same thing lmao
                # print(circleArea)
                p = cntArea / circleArea
                print(p)
                areas[p] = con
                # print(areas[p])

            cv2.imshow("Circles", circles)
            if(len(areas) == 0):
                continue

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

        if cv2.waitKey(1) & 0xFF == ord("q"):
            util.updateLiveRange(rangeName, (hL, sL, vL), (hU, sU, vU))

            util.setManipulation("erode", erode)
            util.setManipulation("dilate", dilate)
            util.setManipulation("blur", blur)

            break

    vs.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    main()
