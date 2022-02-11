import numpy as np
import cv2
import imutils
import math
import util


def main():
    rangeName = input("Range: ")

    util.setupDefaultSliderWindow("hsv", "Trackbars", rangeName)

    vc = cv2.VideoCapture(1)

    print(int(vc.get(cv2.CAP_PROP_FRAME_WIDTH)))
    print(int(vc.get(cv2.CAP_PROP_FRAME_HEIGHT)))

    vc.set(cv2.CAP_PROP_EXPOSURE, -4)

    while True:
        ret, frame = vc.read()

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

        cntsFrame = frame.copy()
        circlesFrame = frame.copy()

        frames = [(frame, "Original"), (mask, "Binary"),
                  (cntsFrame, "Contours"), (circlesFrame, "Circles")]

        if len(cnts) > 0:
            cv2.drawContours(image=cntsFrame, contours=cnts, contourIdx=-1,
                             color=(0, 255, 0), thickness=2, lineType=cv2.LINE_AA)

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
                util.showFrames(frames)
                continue

            # print(areas)
            # print(int(greatestArea))
            c = max(circles, key=cv2.contourArea)
            M = cv2.moments(c)
            center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
            halfWidth = int(vc.get(cv2.CAP_PROP_FRAME_WIDTH))/2
            dFromY = abs(center[0] - halfWidth)

            ((x, y), radius) = cv2.minEnclosingCircle(c)
            diameter = radius * 2
            dX = (util.focalLength * util.ballDiameter) / diameter
            dY = (dX * dFromY) / util.focalLength
            angle = round(math.atan(dY / dX) * (180 / math.pi), 2)
            distance = round(math.sqrt((dX**2) + (dY**2)), 2)

            dIX = (util.focalLength * util.ballDiameterI) / diameter
            dIY = (dIX * dFromY) / util.getFocalLength
            distanceI = round(math.sqrt((dIX**2) + (dIY**2)))

            comparisonError = round(
                int(cv2.contourArea(c)) / (math.pi * (radius**2)) * 100, 2)

            # print(center)

            cv2.circle(frame, (int(x), int(y)), int(radius),
                       (0, 255, 255), 2)
            cv2.putText(frame, f"d: {distanceI}", (0, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(frame, f"0: {angle}", (0, 60),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)

            cv2.putText(cntsFrame, f"Error: {comparisonError}", (0, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(cntsFrame, f"Draw A: {round(math.pi * (radius**2), 2)}", (0, 60),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(cntsFrame, f"Cnt A: {round(int(cv2.contourArea(c)), 2)}", (
                0, 90), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            # cv2.circle(frame, center, 5, (0, 0, 255), -1)

            # TEMP FOR FOCAL LENGTH

            # knownDistance = 72  # In Inches

            # focalLength = util.getFocalLength(
            #     knownDistance, util.ballDiameterI, diameter)
            # cv2.putText(frame, f"F: {focalLength}", (0, 90),
            #             cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)

        util.showFrames(frames)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            util.updateLiveRange(rangeName, (hL, sL, vL), (hU, sU, vU))

            util.setAllManipulation(erode, dilate, blur, minArea, circ)
            break

    vc.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    main()
