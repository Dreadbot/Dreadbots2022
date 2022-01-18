import cv2
import json
from matplotlib.pyplot import hsv
import numpy as np


def callback(value):
    pass


def setupSlider(mode):
    cv2.namedWindow("Trackbars", 0)

    for i in ["MIN", "MAX"]:
        if i == "MIN":
            v = 0
        else:
            v = 255

        for c in mode:
            cv2.createTrackbar(f"{c.upper()}_{i}",
                               "Trackbars", v, 255, callback)


def getSliderValues(mode):
    values = []

    for i in ["MIN", "MAX"]:
        for c in mode:
            value = cv2.getTrackbarPos(f"{c.upper()}_{i}", "Trackbars")
            values.append(value)

    return values


def getModeInput(modeName, validModes):
    mode = ""
    while mode not in validModes:
        mode = input(f"{modeName} Mode: ")
        if mode not in validModes:
            print(f"Please input a valid {modeName} Mode.")

    return mode


def main():
    colorMode = getModeInput("Color", ["hsv", "rgb"])
    imageMode = getModeInput("Image", ["image", "webcam"])

    if imageMode.lower() == "image":
        image = cv2.imread(input("Image Path: "))
    else:
        camera = cv2.VideoCapture(int(input("Camera ID: ")))

    setupSlider(colorMode)

    while True:
        if imageMode.lower() == "webcam":
            ret, image = camera.read()

            if not ret:
                break

        if colorMode.lower() == "hsv":
            frameToThresh = cv2.cvtColor(image.copy(), cv2.COLOR_BGR2HSV)
        else:
            frameToThresh = image.copy()

        v1Min, v2Min, v3Min, v1Max, v2Max, v3Max = getSliderValues(colorMode)

        kernel = (7, 7)

        thresh = cv2.inRange(frameToThresh, (v1Min, v2Min,
                             v3Min), (v1Max, v2Max, v3Max))
        # blur = cv2.GaussianBlur(thresh, kernel, 0)
        # erode = cv2.erode(blur, kernel, iterations=2)
        # dilate = cv2.dilate(erode, kernel, iterations=4)

        # contours, hierarchy = cv2.findContours(image=thresh, mode=cv2.RETR_TREE, method=cv2.CHAIN_APPROX_NONE)
        # image_copy = image.copy()
        # cv2.drawContours(image=image_copy, contours=contours, contourIdx=-1, color=(0, 255, 0), thickness=2, lineType=cv2.LINE_AA)

        cv2.imshow("Original", image)
        cv2.imshow("Thresh", thresh)
        # cv2.imshow("Contours", image_copy)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            if imageMode == "webcam":
                camera.release()
            cv2.destroyAllWindows()

            output = {
                "vMin": (v1Min, v2Min, v3Min),
                "vMax": (v1Max, v2Max, v3Max)
            }

            with open(input("Output File Name: "), "w") as outfile:
                json.dump(output, outfile)

            break


if __name__ == "__main__":
    main()
