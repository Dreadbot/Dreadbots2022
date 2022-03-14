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


# OpenCV Processing
def getMask(frame, lower: tuple, upper: tuple, eIts: int, dIts: int, blurK: int, colorSpace: int = cv2.COLOR_BGR2HSV):
    hsv = cv2.cvtColor(frame, colorSpace)
    
    blurred = cv2.blur(hsv, (blurK, blurK))
    inRange = cv2.inRange(blurred, lower, upper)
    erode = cv2.erode(inRange, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)
    
    return dilate #Convert to the defined colorSpace, blur the image, threshold, erode white pixels, dilate what's left, return that image


# Halve the image (for faster upload)
def res_img(frame):
    h,w,_ = frame.shape

    dst_w = w//2
    dst_h = h//2

    res_frame = cv2.resize(res_frame, (dst_w, dst_h))

    return res_frame

def res_coords(pt):
    u, v = pt
    u = u // 2
    h = h // 2

    

# Sort a 2D array low-to-high based on the first entry of each contained array
def pt_sort(e):
    return(e[0])


# main block
def main():
    # Establish connection to networktables and publish related info to terminal
    logging.basicConfig(level=logging.DEBUG)

    ip = "10.36.56.2"

    NetworkTables.initialize(server=ip)

    def connection_listener(connected, info):
        print(info, "; connected=%s" % connected)

    NetworkTables.addConnectionListener(connection_listener, immediateNotify=True)

    table = NetworkTables.getTable('SmartDashboard')
    

    # Enable the camera server to send images to SmartDashboard
    cam_server = CameraServer.getInstance()
    cam_server.enableLogging()

    outputStream = cam_server.putVideo("Turret Camera", 640, 480)



    # Create capture object out of camera 0
    cap = cv2.VideoCapture(0)

    # Disable the webcam's auto exposure (w/ 0.75) and set the exposure to 0.01
    cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.75)
    cap.set(cv2.CAP_PROP_EXPOSURE, 0.01)


    # preload networktables with the last saved settings
    with open('/home/pi/vision/saved_monitors.json', 'r') as f:
        preload = json.load(f)

    for entry in preload:
        table.putNumber(entry, preload[entry])


    # Create real world visualizer object
    rw_vis = rw_visualizer.space(800,800)


    # Init angle & distance vars
    angle = 0
    distance = 0

    # Init y_ignore
    y_ignore = table.getNumber("YIgnore", 0)

    # Main loop
    while True:
        cap_ret, proc_frame = cap.read() # Read from camera, create a processing image and a drawing image
        draw_frame = proc_frame.copy()

        # Highlight the area above the YIgnore more than the area below it
        draw_frame[0:y_ignore, :] += 50
        draw_frame[y_ignore:480, :] += 25

        # Draw image crosshairs
        cv2.line(draw_frame, (320, 0), (320, 480), (255, 255, 255))
        cv2.line(draw_frame, (0, 240), (640, 240), (255, 255, 255))

        draw_frame = res_img(draw_frame) # Halve drawing image

        center_ret = False # Reset target found flag to False every loop


        # If the webcam does not return anything, kill the loop and exit the code
        if not cap_ret:
            break


        # Update HSL & OpenCV ranges with most recent NT entries
        hue = [table.getNumber("TargetHLowerValue", 0), table.getNumber("TargetHUpperValue", 255)]
        sat = [table.getNumber("TargetSLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        lum = [table.getNumber("TargetLLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]

        mask_lower_bound = (hue[0], lum[0], sat[0])
        mask_upper_bound = (hue[1], lum[1], sat[1])
        
        blur   = max([int(table.getNumber("TurretBlurKernel", 1)),  1]) #max([var, 1]) is used to ensure the entered var is always positive
        erode  = max([int(table.getNumber("TurretErodeKernel", 1)), 1])
        dilate = max([int(table.getNumber("TurretErodeKernel", 1)), 1])
        
        y_ignore = int(table.getNumber("YIgnore", 0)) # Y coordinate at which to ignore any result below
        
        dampen_weight = max([table.getNumber("DampenWeight", 1), 0]) # Filter dampening weight (IIR Filter)

        
        # Process raw image from the webcam through OpenCV processing to output binary image
        mask = getMask(proc_frame, mask_lower_bound, mask_upper_bound, erode, dilate, blur, colorSpace=cv2.COLOR_BGR2HLS)

        # Find contours (areas of low values to high values) in the binary image
        contours = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        contours = imutils.grab_contours(contours)


        # Initialize image point array
        imgpoints = []

        for contour in contours: #Loop through each found contour
            x, y, w, h = cv2.boundingRect(contour) # Return bounding box parameters of the contour


            # Ignore values below defined Y ignore level
            if y >= y_ignore: 
                continue


            # Center the X & Y coordinates to the center of the bounding box
            x += (w//2)
            y += (h//2)


            # If the contour's bounding box is more than 30px wide or tall ignore it
            if w < 30 and h < 30:
                cv2.circle(draw_frame, (x, y), 3, (0,0,255), thickness=-1) #Draw contour dot


                # Create current point array and append to image points array
                pt = [x, y]
                imgpoints.append(pt)


        # Sort the image points array to index points left-to-right based on X position
        imgpoints.sort(reverse=True, key=pt_sort)


        # Unwrap each point to draw on real-world visualizer
        for pt in imgpoints:
            u, v = pt
            x, y, _ = projection_math.similar_triangles_calculation(u, v)
            rw_vis.circle((x,y), (0,0,255))


        # If enough image points exist, process the target center using the orthogonal bisector intersection method
        # If not, attempt single point calculation
        # If none, update target found bool accordingly
        if len(imgpoints) > 2:
            center_ret, angle, distance = projection_math.orth_bisector_calculation(imgpoints, 
                                                            dampen=dampen_weight, 
                                                            prev=(angle, distance), 
                                                            draw_target=draw_frame, 
                                                            visualizer=rw_vis)
        elif len(imgpoints) == 1:
            center_ret, angle, distance = projection_math.single_point(imgpoints[0])
        else:
            center_ret = False


        # If a target center is not succesfully returned, set angle & distance to 0 and tell SmartDashboard no targets were found
        if not center_ret:
            angle = 0
            distance = 0


        # Update NT
        table.putBoolean("TargetFoundInFrame", center_ret)
        table.putNumber("RelativeDistanceToHub", distance)
        table.putNumber("RelativeAngleToHub", angle)


        # Set the output of the camera server to the selected camera
        # 0 - "clean" image
        # 1 - binary mask w/ "clean" underlay
        # 2 - Real-world visualizer
        if table.getNumber("CameraSelection", 0) == 0:
            outputStream.putFrame(draw_frame)

        elif table.getNumber("CameraSelection", 0) == 1:
            mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
            mask = cv2.addWeighted(mask, 0.6, frame, 0.4, 1.0)
            mask = res_img(mask)

            w, h, _ = mask.shape

            cv2.line(mask, (w//2,0), (w//2,h), (0,0,255))

            outputStream.putFrame(mask)

        elif table.getNumber("CameraSelection", 0) == 2:
            frame = rw_vis.retrieve_img()
            outputStream.putFrame(frame)


    # Release capture object
    cap.release()

if __name__ == "__main__":
    main()
