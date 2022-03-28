import cv2
import util
import circularity
import hough


def find_ball_in_frame(frame, lower, upper, erode, dilate, blur, minCirc, minArea, radiusError, xyError):
    mask = util.getMask(frame, lower, upper, erode, dilate, blur)

    circle = circularity.getBall(mask, minCirc, minArea)
    h = hough.getBall(cv2.blur(mask, (blur + 8, blur + 8), 0))
    circles = []
    if circle is not None:
        circles.append((circle[0], circle[1], circle[2]))

    if h is not None:
        circles.append((h[0], h[1], h[2]))

    filteredCircles = []

    for circle in circles:
        i = circles.index(circle)
        if i + 1 == len(circles):
            continue

        nextCircle = circles[i + 1]

        if abs(circle[2] - nextCircle[2]) < radiusError \
                and abs(circle[0] - nextCircle[0]) < xyError \
                and abs(circle[1] - nextCircle[1]) < xyError:

            avgX = (circle[0] + nextCircle[0]) / 2
            avgY = (circle[1] + nextCircle[1]) / 2
            avgR = (circle[2] + nextCircle[2]) / 2
            c = (avgX, avgY, avgR)

            filteredCircles.append(c)

    if len(filteredCircles) > 0:
        bestCircle = filteredCircles[0]
        for betterC in filteredCircles:
            if betterC[2] > bestCircle[2]:
                bestCircle = betterC

        return bestCircle
    else:
        return None
