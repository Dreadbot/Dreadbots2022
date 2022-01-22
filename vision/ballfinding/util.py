import cv2
import numpy as np
import imutils
import json

rangesFile = "vision\\ballfinding\\Data\\ranges.json"

def getMask(frame, lower: tuple, upper: tuple, eIts = 3, dIts = 7,colorSpace: int = cv2.COLOR_BGR2HSV, blurK = 3, k = 3):
    frame = imutils.resize(frame, width=600)
    hsv = cv2.cvtColor(frame, colorSpace)
    inRange = cv2.inRange(hsv, lower, upper)
    blurred = cv2.GaussianBlur(inRange, (blurK, blurK), 0)

    # kernel = np.ones((k, k), np.uint8)
    erode = cv2.erode(blurred, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)

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

def setupSliderWindow(mode, windowName, lower: tuple = None, upper: tuple = None):
    cv2.namedWindow("Trackbars", 0)

    for i in ["MIN", "MAX"]:
        if lower == None and upper == None:
            if i == "MIN":
                v = 0
            else:
                v = 255

        for c in mode:
            if lower != None and upper != None:
                ind = mode.index(c)
                if i == "MIN":
                    v = lower[ind]
                else:
                    v = upper[ind]

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