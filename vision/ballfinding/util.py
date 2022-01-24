import cv2
import numpy as np
import imutils
import json

dataDir = "Dreadbots2022\\vision\\ballfinding\\Data"
rangesFile = dataDir + "\\ranges.json"
manipFile = dataDir + "\\manipulation.json"


def getMask(frame, lower: tuple, upper: tuple, eIts: int, dIts: int, blurK: int, colorSpace: int = cv2.COLOR_BGR2HSV):
    frame = imutils.resize(frame, width=600)
    hsv = cv2.cvtColor(frame, colorSpace)
    inRange = cv2.inRange(hsv, lower, upper)
    blurred = cv2.blur(inRange, (blurK, blurK), 0)

    erode = cv2.erode(blurred, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)

    return dilate


def getManipulation():
    f = open(manipFile)
    return json.load(f)


def setManipulation(key, value: int):
    data = getManipulation()

    if key not in data.keys():
        return

    data[key] = value
    with open(manipFile, "w") as outfile:
        json.dump(data, outfile)

    return data[key]


def isLiveRange(key):
    f = open(rangesFile)
    jsonData: dict = json.load(f)
    return key in jsonData.keys()


def getLiveRange(key):
    f = open(rangesFile)
    jsonData = json.load(f)

    return jsonData[key]


def updateLiveRange(key, lower: tuple, upper: tuple):
    f = open(rangesFile)
    jsonData = json.load(f)
    jsonData[key] = {
        "lower": lower,
        "upper": upper
    }

    with open(rangesFile, "w") as outfile:
        json.dump(jsonData, outfile)

    return jsonData[key]


def setupSliderWindow(mode, windowName, lower: tuple = (0, 0, 0), upper: tuple = (255, 255, 255), erode=0, dilate=0, blur=0):
    cv2.namedWindow("Trackbars", 0)

    for i in ["MIN", "MAX"]:
        for c in mode:
            ind = mode.index(c)
            if i == "MIN":
                v = lower[ind]
            else:
                v = upper[ind]

            cv2.createTrackbar(f"{c.upper()}_{i}",
                               windowName, v, 255, callback)

    for it in ["Erosion", "Dilation"]:
        if it == "Erosion":
            v = erode
        else:
            v = dilate
        cv2.createTrackbar(f"{it}_Iterations", windowName, v, 30, callback)

    cv2.createTrackbar("Blur_Kernel", windowName, blur, 10, callback)


def getSliderValues(mode, windowName):
    values = []

    for i in ["MIN", "MAX"]:
        for c in mode:
            values.append(cv2.getTrackbarPos(f"{c.upper()}_{i}", windowName))

    for it in ["Erosion", "Dilation"]:
        values.append(cv2.getTrackbarPos(f"{it}_Iterations", windowName))

    values.append(cv2.getTrackbarPos("Blur_Kernel", windowName))

    return values


def callback(value):
    pass
