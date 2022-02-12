import cv2
import numpy as np
import imutils
import json
import math
from networktables import NetworkTables
import threading
import os

dataDir = os.path.join("vision", "ballfinding", "Data")
rangesFile = os.path.join(dataDir, "ranges.json")
manipFile = os.path.join(dataDir, "manipulation.json")

ballDiameter = 0.24  # In Meters
ballDiameterI = 9.5  # In Inches
cameraHight = 0.58  # In Meters
focalLength = 667  # In Pixels

staticIp = "10.36.56.11"


def connectToNetworkTable():
    condition = threading.Condition()
    notified = False

    def listener(connected, info):
        print(f"{info}\nConnected: {connected}")
        condition.notify()
        notified = True

    NetworkTables.initialize(staticIp)
    NetworkTables.addConnectionListener(listener, immediateNotify=True)

    if not notified:
        condition.wait()

    print("Connected to Network Table")

    return NetworkTables.getTable('SmartDashboard')


def getMask(frame, lower: tuple, upper: tuple, eIts: int, dIts: int, blurK: int, colorSpace: int = cv2.COLOR_BGR2HSV):
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


def setAllManipulation(erode, dilate, blur, minArea, minCirc):
    setManipulation("erode", erode)
    setManipulation("dilate", dilate)
    setManipulation("blur", blur)
    setManipulation("area", minArea)
    setManipulation("circ", minCirc)


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


def setupDefaultSliderWindow(mode, windowName, rangeName):
    if not isLiveRange(rangeName):
        updateLiveRange(rangeName, (0, 0, 0), (255, 255, 255))

    range = getLiveRange(rangeName)
    manip = getManipulation()

    setupSliderWindow(
        mode, windowName, range["lower"], range["upper"], manip["erode"], manip["dilate"], manip["blur"], manip["area"], manip["circ"])


def setupSliderWindow(mode, windowName, lower: tuple = (0, 0, 0), upper: tuple = (255, 255, 255), erode=0, dilate=0, blur=0, area=1, circ=1):
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

    cv2.createTrackbar("Blur_Kernel", windowName, blur, 30, callback)
    cv2.setTrackbarMin("Blur_Kernel", windowName, 1)
    # cv2.setTrackbarMax("Blur_Kernel", windowName, 30)

    cv2.createTrackbar("Min_Area", windowName, area, 100000, callback)
    cv2.setTrackbarMin("Min_Area", windowName, 1)
    # cv2.setTrackbarMax("Min_Area", windowName, 100000)

    cv2.createTrackbar("Min_Circ", windowName, circ, 100, callback)
    cv2.setTrackbarMin("Min_Circ", windowName, 1)
    # cv2.setTrackbarMax("Min_Circ", windowName, 100)


def getSliderValues(mode, windowName):
    values = []

    for i in ["MIN", "MAX"]:
        for c in mode:
            values.append(cv2.getTrackbarPos(f"{c.upper()}_{i}", windowName))

    for it in ["Erosion", "Dilation"]:
        values.append(cv2.getTrackbarPos(f"{it}_Iterations", windowName))

    values.append(cv2.getTrackbarPos("Blur_Kernel", windowName))

    for m in ["Area", "Circ"]:
        values.append(cv2.getTrackbarPos(f"Min_{m}", windowName))

    return values


def showFrames(frames):
    if not frames:
        return

    for frame in frames:
        cv2.imshow(frame[1], frame[0])


def getFocalLength(kDistance, width, pixels):
    return (kDistance * pixels) / width


def getDistance(src, x, radius, focalLength, ballDiameter):
    dFromY = abs(x - (int(src.shape[1])/2))
    diameter = radius * 2
    dY = (focalLength * ballDiameter) / diameter
    dX = (dY * dFromY) / focalLength
    angle = round(math.atan(dX / dY) * (180 / math.pi), 2)
    distance = round(math.sqrt((dY**2) + (dX**2)), 2)
    return distance, angle


def callback(value):
    pass
