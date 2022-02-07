import numpy as np
import cv2
import imutils
import math
import util


def main():
    rangeName = input("Range: ")

    util.setupDefaultSliderWindow("hsv", "Trackbars", rangeName)

    vs = cv2.VideoCapture(1)
    vs.set(cv2.CAP_PROP_EXPOSURE, -4)

    while True:
        ret, frame = vs.read()

        if not ret:
            break

        # frame = imutils.resize(frame, width=600)

        hL, sL, vL, hU, sU, vU, erode, dilate, blur, minArea, circ = util.getSliderValues(
            "hsv", "Trackbars")

        minCirc = circ / 100
        # print(minCirc)

        lower = (hL, sL, vL)
        upper = (hU, sU, vU)

        mask = util.getMask(frame, lower, upper, erode, dilate, blur)

        cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        if len(cnts) > 0:
            image = frame.copy()
            cv2.drawContours(image=image, contours=cnts, contourIdx=-1,
                             color=(0, 255, 0), thickness=2, lineType=cv2.LINE_AA)

            circlesFrame = frame.copy()

            circles = []
            for con in cnts:
                perimeter = cv2.arcLength(con, True)
                area = cv2.contourArea(con)
                # print(int(area))
                if perimeter == 0 or int(area) < minArea:
                    continue

                circularity = (4*math.pi*area)/(perimeter**2)
                if minCirc < circularity <= 1.00:
                    circles.append(con)
                    ((x, y), radius) = cv2.minEnclosingCircle(con)
                    cv2.circle(circlesFrame, (int(x), int(y)),
                               int(radius), (0, 0, 0), 2)
                    cv2.putText(circlesFrame, str(circularity), (int(x), int(
                        y)), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 0), 2, cv2.LINE_AA)

            if(len(circles) == 0):
                continue

            # print(areas)
            # print(int(greatestArea))
            c = max(circles, key=cv2.contourArea)
            M = cv2.moments(c)
            center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
            halfWidth = (frame.shape[1])/2
            dFromY = abs(halfWidth - center[0])

            ((x, y), radius) = cv2.minEnclosingCircle(c)
            diameter = radius * 2
            dX = (util.focalLength * util.ballDiameter) / diameter
            dY = (dX * dFromY) / util.focalLength
            angle = round(math.degrees(math.atan(dY / dX)), 2)
            distance = round(math.sqrt((dX**2) + (dY**2)), 2)
            distanceI = distance * 39.4

            # print(center)

            cv2.circle(frame, (int(x), int(y)), int(radius),
                       (0, 255, 255), 2)
            cv2.putText(frame, f"d: {distanceI}", (0, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(frame, f"0: {angle}", (0, 60),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            # cv2.circle(frame, center, 5, (0, 0, 255), -1)

        cv2.imshow("Frame", frame)
        cv2.imshow("Cnts", image)
        cv2.imshow("Mask", mask)
        cv2.imshow("Circles", circlesFrame)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            util.updateLiveRange(rangeName, (hL, sL, vL), (hU, sU, vU))

            util.setAllManipulation(erode, dilate, blur, minArea, circ)
            break

    vs.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    main()
