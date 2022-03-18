# CREDIT TO SuperXLink

# Variable Definitions
import math
import time
import cv2
from cv2 import threshold
from cv2 import FONT_ITALIC
from cv2 import WINDOW_FREERATIO
import numpy as np
import argparse


def main():
    cv = cv2
    pi = math.pi
    r = 1
    max_value = 255
    max_value_H = 360//2
    low_H = 95  # Blue 95 #Red 0
    low_S = 75  # Blue 75 #Red 94
    low_V = 38  # Blue 38 #Red 127
    high_H = 128  # Blue 128 #Red 14
    high_S = 234  # Blue 234 #Red 255
    high_V = 255  # Blue 255 #Red 255
    window_capture_name = 'Video Capture'
    window_detection_name = 'Object Detection'
    low_H_name = 'Low H'
    low_S_name = 'Low S'
    low_V_name = 'Low V'
    high_H_name = 'High H'
    high_S_name = 'High S'
    high_V_name = 'High V'

    # Capturing Camera Input
    camera = cv2.VideoCapture(0)
    camera.set(cv2.CAP_PROP_EXPOSURE, -4)

    # HSV Thresholding Trackbars
    def on_low_H_thresh_trackbar(val):
        global low_H
        global high_H
        low_H = val
        low_H = min(high_H-1, low_H)
        cv.setTrackbarPos(low_H_name, window_detection_name, low_H)

    def on_high_H_thresh_trackbar(val):
        global low_H
        global high_H
        high_H = val
        high_H = max(high_H, low_H+1)
        cv.setTrackbarPos(high_H_name, window_detection_name, high_H)

    def on_low_S_thresh_trackbar(val):
        global low_S
        global high_S
        low_S = val
        low_S = min(high_S-1, low_S)
        cv.setTrackbarPos(low_S_name, window_detection_name, low_S)

    def on_high_S_thresh_trackbar(val):
        global low_S
        global high_S
        high_S = val
        high_S = max(high_S, low_S+1)
        cv.setTrackbarPos(high_S_name, window_detection_name, high_S)

    def on_low_V_thresh_trackbar(val):
        global low_V
        global high_V
        low_V = val
        low_V = min(high_V-1, low_V)
        cv.setTrackbarPos(low_V_name, window_detection_name, low_V)

    def on_high_V_thresh_trackbar(val):
        global low_V
        global high_V
        high_V = val
        high_V = max(high_V, low_V+1)
        cv.setTrackbarPos(high_V_name, window_detection_name, high_V)

    # Creating the Trackbars
    cv.namedWindow(window_detection_name, cv2.WINDOW_NORMAL)

    # Resizing Window Length LONG BOI
    cv2.resizeWindow(window_detection_name, 1680, 20)

    # Create Trackbar pt. 2
    cv.createTrackbar(low_H_name, window_detection_name,
                      low_H, max_value_H, on_low_H_thresh_trackbar)
    cv.createTrackbar(high_H_name, window_detection_name,
                      high_H, max_value_H, on_high_H_thresh_trackbar)
    cv.createTrackbar(low_S_name, window_detection_name,
                      low_S, max_value, on_low_S_thresh_trackbar)
    cv.createTrackbar(high_S_name, window_detection_name,
                      high_S, max_value, on_high_S_thresh_trackbar)
    cv.createTrackbar(low_V_name, window_detection_name,
                      low_V, max_value, on_low_V_thresh_trackbar)
    cv.createTrackbar(high_V_name, window_detection_name,
                      high_V, max_value, on_high_V_thresh_trackbar)

    cnt = 0

    # Defines first update
    firstTime = True

    # Camera Processing
    while True:
        ret, frame = camera.read()
        # Extra cv2.imshow("default",frame)

        if not ret:
            break

        cnt += 1

        # TIMING DIAL FOR HOW MANY UPDATES PER SECOND
        # extra time.sleep(0.25)

        # Extra Gaussian = cv2.GaussianBlur(frame, (33, 33),0)
        # Extra grey_image = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        half = cv2.resize(frame, (0, 0), fx=0.6, fy=0.6)
        # Extra cv2.imshow("half",half)
        hsvImage = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        # Extra cv2.imshow("hsvimage",hsvImage)
        frameThreshold = cv.inRange(
            hsvImage, (low_H, low_S, low_V), (high_H, high_S, high_V))
        # Extra cv2.imshow("frameThreshold",frameThreshold)
        Gaussian = cv2.GaussianBlur(frameThreshold, (31, 31), 0)

        kernel = np.ones((5, 5), np.uint8)
        img_erosion = cv2.erode(Gaussian, kernel, iterations=7)
        img_dilation = cv2.dilate(img_erosion, kernel, iterations=5)

        half2 = cv2.resize(img_dilation, (0, 0), fx=0.5, fy=0.5)

        edged = cv2.Canny(img_dilation, 30, 200)
        contours, hierarchy = cv2.findContours(edged,
                                               cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
        cv2.drawContours(img_dilation, contours, -1, (0, 255, 0), 3)

        # Apply Hough transform on the blurred image.
        detected_circles = cv2.HoughCircles(half2,
                                            cv2.HOUGH_GRADIENT, 1, 20, param1=50,
                                            param2=30, minRadius=10, maxRadius=1000)

        # Draw circles that are detected.
        if detected_circles is not None:

            detected_circles = np.uint16(np.around(detected_circles))
            for pt in detected_circles[0, :]:
                x, y, r = pt[0], pt[1], pt[2]
            cv2.circle(half2, (x, y), r, (255, 0, 0), 2)
            cv2.circle(half2, (x, y), 1, (0, 0, 255), 3)
            #Extra: cv2.imshow("Detected Circle", half2)

            # Print Circle Values
            cv2.putText(half2, f"{x}, {y}, {r}", (x+50, y),
                        FONT_ITALIC, 1, (255, 255, 255), 5, cv2.LINE_AA)

            # calibration
            #cv2.inRange(img_dilation, (61, 87, 26), (73, 255, 255))

        # Displaying Camera Capture
        cv2.imshow("Raw (half)", half)
        if firstTime:
            cv2.moveWindow("Raw (half)", 900, 300)

        cv2.imshow("HSV Converted (half2)", half2)
        if firstTime:
            cv2.moveWindow("HSV Converted (half2)", 230, 300)

        # Distance Find + MEASUREMENT DISPLAY
        areaat1meter = 11209
        area = r**2*pi
        yaxis = .13335  # the height difference betweent he camera lens and the top of circle
        if areaat1meter != 0 and area != 0:
            hyp = math.sqrt((1/(area/areaat1meter)))
        else:
            continue

        #Extra: print("hyp = "+ str(hyp))
        HalfBall = 4.5
        RobotHeight = 23  # Can and WILL Change
        MainHeight = RobotHeight - HalfBall
        distance = math.sqrt(((hyp**2)-(yaxis**2)))

        PythagoreanDistance = math.sqrt(
            abs((distance*distance)-(MainHeight*MainHeight)))

        # AREA PRINT
        img2 = np.zeros((112, 212, 3), np.uint8)
        font = cv2.FONT_HERSHEY_SIMPLEX
        topLeftCornerOfText = (10, 30)
        fontScale = 1
        fontColor = (255, 255, 255)
        thickness = 1
        lineType = 2

        # Rounding
        area = round(area, 3)

        # Text Info
        cv2.putText(img2, str(area),
                    topLeftCornerOfText,
                    font,
                    fontScale,
                    fontColor,
                    thickness,
                    lineType)

        # Showing Area Window
        cv2.imshow("Area", img2)
        if firstTime:
            cv2.moveWindow("Area", 0, 462)

        # DISTANCE PRINT
        img = np.zeros((112, 212, 3), np.uint8)
        font = cv2.FONT_HERSHEY_SIMPLEX
        topLeftCornerOfText = (10, 30)
        fontScale = 1
        fontColor = (255, 255, 255)
        thickness = 1
        lineType = 2

        # Rounding
        distance = round(distance, 3)

        # Text Info
        cv2.putText(img, str(distance),
                    topLeftCornerOfText,
                    font,
                    fontScale,
                    fontColor,
                    thickness,
                    lineType)

        # Showing Distance Window
        cv2.imshow("Distance", img)
        if firstTime:
            cv2.moveWindow("Distance", 0, 300)
        print(cnt)
        # Printing the HSV Threshold Values
        print("low hue: " + str(low_H) + "\nlow saturation: " + str(low_S) + "\nlow value" +
              "\nhigh hue: " + str(high_H) + "\nhigh saturation: " + str(high_S) + "\nhigh value: " + str(high_V))

        # Printing the HSV Thresholding Values and Breaking on "q" Press
        if cv2.waitKey(1) & 0xFF == ord("q"):
            print("low hue: " + str(low_H) + "\nlow saturation: " + str(low_S) + "\nlow value" +
                  "\nhigh hue: " + str(high_H) + "\nhigh saturation: " + str(high_S) + "\nhigh value: " + str(high_V))

            break

        # Defines that First Time has Passed
        firstTime = False

    # Breaking the Windows and Cameras at the end
    camera.release()
    cv2.destroyAllWindows()


def getBall(src):
    detected_circles = cv2.HoughCircles(src,
                                        cv2.HOUGH_GRADIENT, 1, 20, param1=50,
                                        param2=30, minRadius=10, maxRadius=1000)

    # Draw circles that are detected.
    if detected_circles is not None:
        detected_circles = np.uint16(np.around(detected_circles))
        for pt in detected_circles[0, :]:
            return pt[0], pt[1], pt[2]

    return None


if __name__ == "__main__":
    main()
