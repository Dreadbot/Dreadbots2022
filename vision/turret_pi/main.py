import cv2
import argparse
import os
import json
from cv2 import dilate
import imutils
from networktables import NetworkTables
import threading
import projection_math
from cscore import CameraServer
import time, sys, logging
import numpy as np
import rw_visualizer

import time

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
    
    blurred = cv2.blur(hsv, (blurK, blurK))
    inRange = cv2.inRange(blurred, lower, upper)
    erode = cv2.erode(inRange, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)
    
    return dilate

def resImg(frame):
    h,w,_ = frame.shape
    dstW = w//2
    dstH = h//2
    resFrame = cv2.resize(frame, (dstW, dstH))
    return resFrame

def pt_sort(e):
    return(e[0])

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

    cs.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.75)
    cs.set(cv2.CAP_PROP_EXPOSURE, 0.01)

    focalLength = 667

    if table is not None:
        table.putNumber("TargetHLowerValue", 50)
        table.putNumber("TargetHUpperValue", 150)
        table.putNumber("TargetLLowerValue", 100)
        table.putNumber("TargetLUpperValue", 255)
        table.putNumber("TargetSLowerValue", 200)
        table.putNumber("TargetSUpperValue", 255)
        table.putNumber("CameraSelection", 1)
        table.putNumber("DampenWeight", 1)
        table.putNumber("YIgnore", 1)


    dampen_weight = table.getNumber("DampenWeight", 1)

    rw_vis = rw_visualizer.space(800,800)

    prev_angle = 0
    prev_distance = 0

    angle = 0
    distance = 0

    while True:

        ret, frame = cs.read()


        if not ret:
            break

        # hL, sL, vL, hU, sU, vU, erode, dilate, blur = getSliderValues(
        #     "Trackbars")
        hue = [table.getNumber("TargetHLowerValue", 0), table.getNumber("TargetHUpperValue", 255)]
        sat = [table.getNumber("TargetSLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        lum = [table.getNumber("TargetLLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        blur = max([int(table.getNumber("TurretBlurKernel", 1)), 1])
        erode = int(table.getNumber("TurretErodeKernel", 1))
        dilate = int(table.getNumber("TurretErodeKernel", 1))
        y_ignore = int(table.getNumber("YIgnore", 0))
        data = getData()
        #(hL, sL, vL) = data["lower"]
        #(hU, sU, vU) = data["upper"]
        #erode = data["erode"]
        #dilate = data["dilate"]
        #blur = data["blur"]

        mask = getMask(frame, (hue[0], lum[0], sat[0]), (hue[1], lum[1], sat[1]), erode, dilate, blur, colorSpace=cv2.COLOR_BGR2HLS)
        cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)
        frame[::] += 50
        xs = []
        ys = []

        true_xs = []
        true_ys = []

        scale_step = 30

        cv2.line(frame, (0, y_ignore), (640, y_ignore), (255,255,255))
        # print("---")
        for c in cnts:
            x, y, w, h = cv2.boundingRect(c)

            if y >= y_ignore:
                continue

            x += (w//2)
            y += (h//2)
            # cv2.putText(mask, str(p), (x + 30, y + 30),
            #             cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2, cv2.LINE_AA)
            if 0 < w < 30 and 0 < h < 15:
                cv2.circle(frame, (x, y), 3, (0,0,255), thickness=-1)
                xs.append(x)
                ys.append(y)
                # # wvec = projection_math.pinhole_angle_calculation((x, y), frame, scale_step)
                # # wvec = projection_math.camera_to_world(x, y)
                # wvec = projection_math.similar_triangles_calculation(x, y)
                # u, v = projection_math.reverse_point(wvec, round=True)
                # cvec = (u, v)
                # # print(cvec)
                # test_wvec = projection_math.pinhole_angle_calculation((x,y), frame, scale_step)
                # # print(f"u,v[ {x, y} ]   wvec[ {test_wvec[::]} ]  triangle_wvec[ {wvec[::]} ]")
                # print(wvec)
                # cv2.circle(frame, cvec, 5, (0,255,255), thickness=-1)
                # scale_step += 15
                # # time.sleep(0.5) #REMOVE THIS LATER

                cv2.circle(frame, (x, y), 3, (0,0,0), thickness=-1)

        center_ret = False
        y_range = 15
        x_range = 50
        add_cnt = 2
        
        
#        for point in range(len(xs)):
#            px, py = xs[point], ys[point]
#            
#            cv2.circle(frame, (px, py), 3, (0,255,255), thickness=-1)
#            cv2.line(frame, (px-x_range, py), (px+x_range, py), (0,255,255), thickness=3)
        imgpoints = []
        for i in range(len(xs)):
            imgpoints.append([xs[i], ys[i]])
        
        imgpoints.sort(reverse=True, key=pt_sort)

        for pt in imgpoints:
            u, v = pt
            x, y, z = projection_math.similar_triangles_calculation(u, v)
            rw_vis.circle((x,y), (0,0,255))

        if len(xs) > 2:
            # angle, distance = projection_math.point_center(xs, ys, frame)
            # p1 = (xs[0], ys[0])
            # p2 = (xs[1], ys[1])
            # center_ret, angle, distance = projection_math.geometric_true_center(p1, p2, draw_target=frame)
            cvec = projection_math.orth_bisector_calculation(imgpoints, draw_target=frame, visualizer=rw_vis)

            # rw_vis.circle(cvec, (0,255,0))
            cv2.circle(frame, cvec, 5, (255,0,0), thickness=-1)

            table.putBoolean("TargetFoundInFrame", True)
        else:
            table.putBoolean("TargetFoundInFrame", False)

            # cv2.circle(frame, (int(ax), int(ay)), 3, (123,213,150), thickness=-1)
        
        # center_ret = (angle is not 0)

        # if not center_ret:
        #     angle = 0
        #     distance = 0
        #     table.putBoolean("TargetFoundInFrame", False)

        if table is not None:
            # table.putBoolean("TargetFoundInFrame", center_ret)
            # if center_ret:
            table.putNumber("RelativeDistanceToHub", distance)
            table.putNumber("RelativeAngleToHub", angle)

        if cs is not None:
            # frame = np.concatenate((frame, mask), axis=1)
            # frame = mask
            # frame[::] += 50
            if table.getNumber("CameraSelection", 0) == 0:
                cv2.line(frame, (320, 0), (320,480), (0,0,255))
                frame = resImg(frame)
                outputStream.putFrame(frame)
            elif table.getNumber("CameraSelection", 0) == 1:
                mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
                mask = cv2.addWeighted(mask, 0.8, frame, 0.2, 0.0)
                mask = resImg(mask)
                w, h, _ = mask.shape
                cv2.line(mask, (w//2,0), (w//2,h), (0,0,255))
                outputStream.putFrame(mask)
            elif table.getNumber("CameraSelection", 0) == 2:
                frame = rw_vis.retrieve_img()
                outputStream.putFrame(frame)

        # cv2.imshow("Frame", frame)
        # cv2.imshow("Binary", mask)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            updateData((hL, sL, vL), (hU, sU, vU), erode, dilate, blur)
            break

    cs.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
