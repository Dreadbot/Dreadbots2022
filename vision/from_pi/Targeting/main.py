import cv2
import argparse
import os
import json
from cv2 import dilate
import imutils
from networktables import NetworkTables
import threading
import colemath
from cscore import CameraServer
import time, sys, logging
import numpy as np

dataDir = os.path.join("vision", "Targeting", "Data")

hue = [25, 90]
sat = [75, 255]
lum = [35, 150]

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

    return inRange

def resImg(frame):
    h,w,_ = frame.shape
    dstW = w//2
    dstH = h//2
    resFrame = cv2.resize(frame, (dstW, dstH))
    return resFrame

def main():
    argparser = argparse.ArgumentParser()
    argparser.add_argument('-p', '--data-path',
                           action='store', dest='datapath')
    args = argparser.parse_args()


    if args.datapath is not None:
        global dataDir
        dataDir = args.datapath

    logging.basicConfig(level=logging.DEBUG)

    ip = "10.36.56.2"

    NetworkTables.initialize(server=ip)

    def connection_listener(connected, info):
        print(info, "; connected=%s" % connected)

    NetworkTables.addConnectionListener(connection_listener, immediateNotify=True)

    table = NetworkTables.getTable('SmartDashboard')
    
    if table is not None:
        cs = CameraServer.getInstance()
        cs.enableLogging()

        outputStream = cs.putVideo("Turret Camera", 640, 480)
    else:
        cs = None

    cs = cv2.VideoCapture(0)

    cs.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
    cs.set(cv2.CAP_PROP_EXPOSURE, -15)

    focalLength = 667

    if table is not None:
        table.putNumber("TargetHLowerValue", 54)
        table.putNumber("TargetHUpperValue", 74)
        table.putNumber("TargetLLowerValue", 234)
        table.putNumber("TargetLUpperValue", 255)
        table.putNumber("TargetSLowerValue", 139)
        table.putNumber("TargetSUpperValue", 255)
        table.putNumber("CameraSelection", 1)
        table.putNumber("DampenWeight", 1)

    dampen_weight = table.getNumber("DampenWeight", 1)

    prev_dist = 0

    while True:

        ret, frame = cs.read()


        if not ret:
            break

        # hL, sL, vL, hU, sU, vU, erode, dilate, blur = getSliderValues(
        #     "Trackbars")
        hue = [table.getNumber("TargetHLowerValue", 0), table.getNumber("TargetHUpperValue", 255)]
        sat = [table.getNumber("TargetSLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        lum = [table.getNumber("TargetLLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        data = getData()
        (hL, sL, vL) = data["lower"]
        (hU, sU, vU) = data["upper"]
        erode = data["erode"]
        dilate = data["dilate"]
        blur = data["blur"]

        mask = getMask(frame, (hue[0], lum[0], sat[0]), (hue[1], lum[1], sat[1]), erode, dilate, blur, colorSpace=cv2.COLOR_BGR2HLS)
        cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        xs = []
        ys = []

        true_xs = []
        true_ys = []

        for c in cnts:
            x, y, w, h = cv2.boundingRect(c)

            x += (w//2)
            y += (h//2)
            # cv2.putText(mask, str(p), (x + 30, y + 30),
            #             cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2, cv2.LINE_AA)
            if 5 < w < 30 and 5 < h < 15:
                # cv2.circle(frame, (x, y), 3, (0,0,255), thickness=-1)
                xs.append(x)
                ys.append(y)

        center_ret = False
        y_range = 15
        x_range_l = 0
        x_range_u = 100
        add_cnt = 2
        
        # print(len(xs))
        for p in range(len(xs)):
            px, py = (xs[p], ys[p])

            cur_add = 0
            for t in range(len(xs)):
                ty = ys[t]
                tx = xs[t]

                diff = abs(py-ty)
                x_diff = abs(px-tx)

                if diff <= y_range and diff != 0 and x_diff < x_range_u:
                    # print("fits")
                    cur_add += 1

            
            if cur_add >= add_cnt:
                true_xs.append(px)
                true_ys.append(py)
                    
        # print(f"{true_xs} {true_ys} \n")
        true_xs = xs
        true_ys = ys
        #for p in range(len(true_xs)):
        #    point = (true_xs[p], true_ys[p])
        #    cv2.circle(frame, point, 3, (255,0,0), thickness=-1)
        
        if len(xs) > 1:
            p1 = (xs[0], ys[0])
            p2 = (xs[1], ys[1])
            cv2.circle(frame, p1, 3, (0,0,255), thickness=-1)
            cv2.circle(frame, p2, 3, (0,0,255), thickness=-1)
            center_ret, angle, distance = colemath.geometric_true_center(p1, p2, draw_target=frame)
            if center_ret:
                cur_dist = dampen_weight*distance + (1-dampen_weight)*prev_dist
                prev_dist = cur_dist
        if not center_ret:
            angle = 0
            distance = 0
            table.putBoolean("TargetFoundInFrame", False)

        if table is not None:
            table.putBoolean("TargetFoundInFrame", center_ret)
            if center_ret:
                table.putNumber("RelativeDistanceToHub", cur_dist)
                table.putNumber("RelativeAngleToHub", angle)

        if cs is not None:
            # frame = np.concatenate((frame, mask), axis=1)
            # frame = mask
            if table.getNumber("CameraSelection", 0) == 0:
                frame = resImg(frame)
                outputStream.putFrame(frame)
            elif table.getNumber("CameraSelection", 0) == 1:
                # mask = resImg(mask)
                mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
                mask = cv2.addWeighted(mask, 0.8, frame, 0.2, 0.0)
                cv2.line(mask, (320,0), (320,480), (0,0,255))
                outputStream.putFrame(mask)

        # cv2.imshow("Frame", frame)
        # cv2.imshow("Binary", mask)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            updateData((hL, sL, vL), (hU, sU, vU), erode, dilate, blur)
            break

    cs.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
