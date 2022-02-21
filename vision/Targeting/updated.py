import cv2
import argparse
import os
import json
from cv2 import dilate
import imutils
from networktables import NetworkTables
import threading
import colemath

dataDir = os.path.join("vision", "Targeting", "Data")


def getData():
    return json.load(open(os.path.join(dataDir, "data.json")))


def updateData(lower: tuple, upper: tuple, erode, dilate, blur):
    data = getData()

    data["lower"] = lower
    data["upper"] = upper
    data["erode"] = erode
    data["dilate"] = dilate
    data["blur"] = blur

    with open(os.path.join(dataDir, "data.json"), "w") as outfile:
        json.dump(data, outfile)

    return data


def setupTrackbars(windowName, mode="hsv"):
    cv2.namedWindow(windowName)

    data = getData()

    def callback(value):
        pass

    for i in ["MIN", "MAX"]:
        for c in mode:
            ind = mode.index(c)
            if i == "MIN":
                v = data["lower"][ind]
            else:
                v = data["upper"][ind]

            cv2.createTrackbar(f"{c.upper()}_{i}",
                               windowName, v, 255, callback)

    for i in ["Erode", "Dilate"]:
        v = data[i.lower()]

        cv2.createTrackbar(f"{i}_Iterations", windowName, v, 30, callback)

    cv2.createTrackbar("Blur", windowName, data["blur"], 30, callback)
    cv2.setTrackbarMin("Blur", windowName, 1)


def getSliderValues(windowName, mode="hsv"):
    values = []
    for i in ["MIN", "MAX"]:
        for c in mode:
            values.append(cv2.getTrackbarPos(f"{c.upper()}_{i}", windowName))

    for i in ["Erode", "Dilate"]:
        values.append(cv2.getTrackbarPos(f"{i}_Iterations", windowName))

    values.append(cv2.getTrackbarPos("Blur", windowName))

    return values


def getMask(frame, lower: tuple, upper: tuple, eIts: int, dIts: int, blurK: int, colorSpace: int = cv2.COLOR_BGR2HSV):
    hsv = cv2.cvtColor(frame, colorSpace)
    inRange = cv2.inRange(hsv, lower, upper)
    blurred = cv2.blur(inRange, (blurK, blurK), 0)

    erode = cv2.erode(blurred, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)

    return dilate


def main():
    argparser = argparse.ArgumentParser()
    argparser.add_argument('-p', '--data-path',
                           action='store', dest='datapath')
    args = argparser.parse_args()

    if args.datapath is not None:
        global dataDir
        dataDir = args.datapath

    # setupTrackbars("Trackbars")

    cond = threading.Condition()
    notified = [False]

    def connectionListener(connected, info):
        print(info, '; Connected=%s' % connected)
        with cond:
            notified[0] = True
            cond.notify()

    NetworkTables.initialize(server="10.36.56.2")
    NetworkTables.addConnectionListener(
        connectionListener, immediateNotify=True)

    with cond:
        print("Waiting")
        if not notified[0]:
            cond.wait()

    print("Connected!")

    table = NetworkTables.getTable('SmartDashboard')

    cs = cv2.VideoCapture(1)

    cs.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
    cs.set(cv2.CAP_PROP_EXPOSURE, -15)

    focalLength = 667

    # if table is not None:
    #     table.putNumber("TargetHLowerValue", 50)
    #     table.putNumber("TargetHUpperValue", 90)
    #     table.putNumber("TargetLLowerValue", 20)
    #     table.putNumber("TargetLUpperValue", 100)
    #     table.putNumber("TargetSLowerValue", 200)
    #     table.putNumber("TargetSUpperValue", 255)

    while True:

        ret, frame = cs.read()

        if not ret:
            break

        # hL, sL, vL, hU, sU, vU, erode, dilate, blur = getSliderValues(
        #     "Trackbars")

        data = getData()
        (hL, sL, vL) = data["lower"]
        (hU, sU, vU) = data["upper"]
        erode = data["erode"]
        dilate = data["dilate"]
        blur = data["blur"]

        mask = getMask(frame, (hL, sL, vL), (hU, sU, vU), erode, dilate, blur)
        cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        targets = []

        for c in cnts:
            x, y, w, h = cv2.boundingRect(c)

            p = cv2.contourArea(c) / (w * h)
            # cv2.putText(mask, str(p), (x + 30, y + 30),
            #             cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2, cv2.LINE_AA)
            if 0.6 < p <= 1.0:
                targets.append(c)

        if len(targets) > 1:
            lowestT = targets[0]
            highestT = targets[0]
            for t in targets:
                M = cv2.moments(t)
                cX = int(M["m10"] / M["m00"])
                cY = int(M["m01"] / M["m00"])

                ML = cv2.moments(lowestT)
                cXL = int(ML["m10"] / ML["m00"])
                cYL = int(ML["m01"] / ML["m00"])

                MH = cv2.moments(highestT)
                cXH = int(MH["m10"] / MH["m00"])
                cYH = int(MH["m01"] / MH["m00"])

                if cX < cXL:
                    lowestT = t
                elif cX > cXH:
                    highestT = t

            ML = cv2.moments(lowestT)
            cXL = int(ML["m10"] / ML["m00"])
            cYL = int(ML["m01"] / ML["m00"])

            MH = cv2.moments(lowestT)
            cXH = int(MH["m10"] / MH["m00"])
            cYH = int(MH["m01"] / MH["m00"])

            angle, distance = colemath.geometric_true_center(
                (cXL, cYL), (cXH, cYH))

            if table is not None:
                table.putNumber("RelativeDistanceToHub", distance)
                table.putNumber("RelativeAngleToHub", angle)

        # cv2.imshow("Frame", frame)
        # cv2.imshow("Binary", mask)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            updateData((hL, sL, vL), (hU, sU, vU), erode, dilate, blur)
            break

    cs.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
