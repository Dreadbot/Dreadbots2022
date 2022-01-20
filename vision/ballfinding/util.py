from appscript import k
import cv2
import imutils
import json

rangesFile = "Data/ranges.json"

def getMask(frame, lower: tuple, upper: tuple, its = 14, colorSpace: int = cv2.COLOR_BGR2HSV):
    frame = imutils.resize(frame, width=600)
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(blurred, colorSpace)

    inRange = cv2.inRange(hsv, lower, upper)
    erode = cv2.erode(inRange, None, iterations=its)
    dilate = cv2.dilate(erode, None, iterations=its)

    return dilate

def isLiveRange(key):
    f = open(rangesFile)
    jsonData:dict = json.load(f)
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

def setupSliderWindow(mode, windowName):
    cv2.namedWindow("Trackbars", 0)

    for i in ["MIN", "MAX"]:
        if i == "MIN":
            v = 0
        else:
            v = 255

        for c in mode:
            cv2.createTrackbar(f"{c.upper()}_{i}",
                               windowName, v, 255, callback)

def getSliderValues(mode, windowName):
    values = []

    for i in ["MIN", "MAX"]:
        for c in mode:
            value = cv2.getTrackbarPos(f"{c.upper()}_{i}", windowName)
            values.append(value)

    return values

def callback(value):
    pass