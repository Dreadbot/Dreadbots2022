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

###########
# POGGERS #
###########

def getMask(frame, lower: tuple, upper: tuple, eIts: int, dIts: int, blurK: int, colorSpace: int = cv2.COLOR_BGR2HSV):
    hsv = cv2.cvtColor(frame, colorSpace)
    
    blurred = cv2.blur(hsv, (blurK, blurK))
    inRange = cv2.inRange(blurred, lower, upper)
    erode = cv2.erode(inRange, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)
    
    return dilate


def res_img(frame):
    h,w,_ = frame.shape
    dstW = w//2
    dstH = h//2
    resFrame = cv2.resize(frame, (dstW, dstH))
    return resFrame

def pt_sort(e):
    return(e[0])

def main():
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

    if table is not None:
        with open('/home/pi/vision/saved_monitors.json', 'r') as f:
            preload = json.load(f)

        for entry in preload:
            table.putNumber(entry, preload[entry])

    rw_vis = rw_visualizer.space(800,800)

    angle = 0
    distance = 0

    while True:
        ret, frame = cs.read()

        center_ret = False

        if not ret:
            break

        hue = [table.getNumber("TargetHLowerValue", 0), table.getNumber("TargetHUpperValue", 255)]
        sat = [table.getNumber("TargetSLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        lum = [table.getNumber("TargetLLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]

        mask_lower_bound = (hue[0], lum[0], sat[0])
        mask_upper_bound = (hue[1], lum[1], sat[1])
        
        blur   = max([int(table.getNumber("TurretBlurKernel", 1)),  1])
        erode  = max([int(table.getNumber("TurretErodeKernel", 1)), 1])
        dilate = max([int(table.getNumber("TurretErodeKernel", 1)), 1])
        
        y_ignore = int(table.getNumber("YIgnore", 0))
        
        dampen_weight = max([table.getNumber("DampenWeight", 1), 0])

        mask = getMask(frame, mask_lower_bound, mask_upper_bound, erode, dilate, blur, colorSpace=cv2.COLOR_BGR2HLS)

        cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        

        xs = []
        ys = []

        imgpoints = []

        for c in cnts:
            x, y, w, h = cv2.boundingRect(c)

            if y >= y_ignore:
                continue

            x += (w//2)
            y += (h//2)

            if w < 30 and h < 30:
                cv2.circle(frame, (x, y), 3, (0,0,255), thickness=-1)
                xs.append(x)
                ys.append(y)

                pt = [x, y]
                imgpoints.append(pt)
                
                cv2.circle(frame, (x, y), 1, (0,0,0), thickness=-1)

        imgpoints.sort(reverse=True, key=pt_sort)

        for pt in imgpoints:
            u, v = pt
            x, y, _ = projection_math.similar_triangles_calculation(u, v)
            rw_vis.circle((x,y), (0,0,255))

        if len(xs) > 2:
            center_ret, angle, distance = projection_math.orth_bisector_calculation(imgpoints, 
                                                            dampen=dampen_weight, 
                                                            prev=(angle, distance), 
                                                            draw_target=frame, 
                                                            visualizer=rw_vis)

        if not center_ret:
            angle = 0
            distance = 0
            table.putBoolean("TargetFoundInFrame", False)

        if table is not None:
            table.putBoolean("TargetFoundInFrame", center_ret)
            table.putNumber("RelativeDistanceToHub", distance)
            table.putNumber("RelativeAngleToHub", angle)

        if cs is not None:
            if table.getNumber("CameraSelection", 0) == 0:
                frame[0:y_ignore, :] += 50
                frame[y_ignore:480, :] += 25
                cv2.line(frame, (320, 0), (320,480), (255,255,255))
                cv2.line(frame, (0,240), (640, 240), (255,255,255))
                frame = res_img(frame)
                outputStream.putFrame(frame)

            elif table.getNumber("CameraSelection", 0) == 1:
                mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
                mask = cv2.addWeighted(mask, 0.8, frame, 0.2, 0.0)
                mask = res_img(mask)

                w, h, _ = mask.shape

                cv2.line(mask, (w//2,0), (w//2,h), (0,0,255))

                outputStream.putFrame(mask)

            elif table.getNumber("CameraSelection", 0) == 2:
                frame = rw_vis.retrieve_img()
                outputStream.putFrame(frame)


    cs.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
