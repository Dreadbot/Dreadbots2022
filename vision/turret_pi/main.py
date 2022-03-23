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
def get_target_mask(frame, lower, upper, eIts, dIts, blurK, colorSpace=cv2.COLOR_BGR2HSV):
    hsv = cv2.cvtColor(frame, colorSpace)
    
    blurred = cv2.blur(hsv, (blurK, blurK))
    inRange = cv2.inRange(blurred, lower, upper)
    erode = cv2.erode(inRange, None, iterations=eIts)
    dilate = cv2.dilate(erode, None, iterations=dIts)

    return dilate #Convert to the defined colorSpace, blur the image, threshold, erode white pixels, dilate what's left, return that image


def get_light_mask(frame, lower, upper, e_its, d_its, blur_k):
    blurred = cv2.blur(frame, (blur_k, blur_k))
    inRange = cv2.inRange(blurred, lower, upper)
    erode = cv2.erode(inRange, None, iterations=e_its)
    dilate = cv2.dilate(erode, None, iterations=d_its)

    inverted = cv2.bitwise_not(dilate)

    return(inverted)


# Halve the image (for faster upload)
def res_img(frame):
    h,w,_ = frame.shape

    dst_w = w//2
    dst_h = h//2

    resFrame = cv2.resize(frame, (dst_w, dst_h))

    return resFrame 


def ranging_selection(imgpoints, pt_range=80):
    ranged_pts = []
    for pt in imgpoints:
        cur_x, cur_y = pt

        successes = 0

        for ref_pt in imgpoints:
            if ref_pt == pt:
                continue
            
            ref_x, ref_y = ref_pt

            dx = abs(cur_x-ref_x)
            dy = abs(cur_y-ref_y)

            if dx < pt_range and dy < pt_range:
                successes += 1
        
        if successes >= 1:
            ranged_pts.append(pt)

    return ranged_pts


# Sort a 2D array low-to-high based on the first entry of each contained array
def pt_sort(e):
    return(e[0])

def init_camera():
    cap = None
    for i in range(10):
        try:
            cap = cv2.VideoCapture(i)
            break
        except:
            continue

    if cap is None:
        print("NO CAMERAS FOUND")
        exit()

    # Disable the webcam's auto exposure (w/ 0.75) and set the exposure to 0.01
    cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.75)
    cap.set(cv2.CAP_PROP_EXPOSURE, 0.01)


    return(cap)


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
    cap = init_camera() 

    
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

    ref_ret, ref_frame = cap.read()
    frame_h, frame_w, frame_d = ref_frame.shape

    white_img = np.zeros([frame_h//2, frame_w//2, frame_d], dtype=np.uint8)

    prev_time = 0

    fps = 18

    # Main loop
    while True:
        cap_ret, frame = cap.read() # Read from camera
        
        # Create a copy of the frame purely for drawing & pushing
        draw_frame = frame.copy()

        # Y coordinate at which to ignore any result below
        y_ignore = int(table.getNumber("YIgnore", 0)) 

        # Size down the pushed image and mix it with a white image to brighten
        draw_frame = res_img(draw_frame)
        draw_frame = cv2.addWeighted(draw_frame, 0.7, white_img, 0.3, 25)
        
        """
        Image is resized before any drawing because resizing after drawing makes the image hard
        to read driver-side
        """

        center_ret = False # Reset target found flag to False every loop


        # If the webcam does not return anything, kill the loop and exit the code
        if not cap_ret:
            break


        # Update HSL & OpenCV ranges with most recent NT entries
        target_hue = [table.getNumber("TargetHLowerValue", 0), table.getNumber("TargetHUpperValue", 255)]
        target_sat = [table.getNumber("TargetSLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]
        target_lum = [table.getNumber("TargetLLowerValue", 0), table.getNumber("TargetSUpperValue", 255)]

        light_B = [table.getNumber("LightBLowerValue", 0), table.getNumber("LightBUpperValue", 255)]
        light_G = [table.getNumber("LightGLowerValue", 0), table.getNumber("LightGUpperValue", 255)]
        light_R = [table.getNumber("LightRLowerValue", 0), table.getNumber("LightRUpperValue", 255)]

        target_mask_lower_bound = (target_hue[0], target_lum[0], target_sat[0])
        target_mask_upper_bound = (target_hue[1], target_lum[1], target_sat[1])

        light_mask_lower_bound = (light_B[0], light_G[0], light_R[0])
        light_mask_upper_bound = (light_B[1], light_G[1], light_R[1])
        
        target_blur   = max([int(table.getNumber("TurretBlurKernel", 1)),  1]) #max([var, 1]) is used to ensure the entered var is always positive
        target_erode  = max([int(table.getNumber("TurretErodeKernel", 1)), 0])
        target_dilate = max([int(table.getNumber("TurretErodeKernel", 1)), 0])

        light_blur   = max([int(table.getNumber("LightBlurKernel", 1)),  1]) #max([var, 1]) is used to ensure the entered var is always positive
        light_erode  = max([int(table.getNumber("LightErodeKernel", 1)), 0])
        light_dilate = max([int(table.getNumber("LightErodeKernel", 1)), 0])
        
        
        
        dampen_weight = max([table.getNumber("DampenWeight", 1), 0]) # Filter dampening weight (IIR Filter)

        
        # Process raw image from the webcam through OpenCV processing to output binary image
        target_mask = get_target_mask(frame, 
                                      target_mask_lower_bound, 
                                      target_mask_upper_bound, 
                                      target_erode, target_dilate, 
                                      target_blur, 
                                      colorSpace=cv2.COLOR_BGR2HLS)

        light_mask  = get_light_mask(frame, 
                                     light_mask_lower_bound, 
                                     light_mask_upper_bound, 
                                     light_erode, 
                                     light_dilate, 
                                     light_blur)

        full_mask = cv2.bitwise_and(target_mask, light_mask)


        # Find contours (areas of low values to high values) in the binary image
        contours = cv2.findContours(full_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
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
            if w > 5 and h > 5:
                # Create current point array and append to image points array
                pt = [x, y]
                imgpoints.append(pt)


        # Sort the image points array to index points left-to-right based on X position
        imgpoints.sort(reverse=True, key=pt_sort)

        try:
            # Loop through and remove any points that are outside of a defined range of Y value
            for i in range(len(imgpoints)):
                pt = imgpoints[i]
                u, v = pt
                
                successes = 0

                for ref_pt in imgpoints:
                    _, ref_v = ref_pt
                    
                    if ref_pt == pt:
                        continue
                    
                    dy = abs(ref_v - v)

                    if dy < 30:
                        successes += 1

                if successes < max([len(imgpoints)-2, 1]):
                    imgpoints.pop(i)
        except IndexError:
            pass


        # Unwrap each point to draw on real-world visualizer
        for pt in imgpoints:
            u, v = pt
            
            cv2.circle(draw_frame, (u//2, v//2), 1, (0,0,255), thickness=-1)

            x, y, _ = projection_math.similar_triangles_calculation(u, v)
            rw_vis.circle((x,y), (0,0,255))


        # If enough image points exist, process the target center using the orthogonal bisector intersection method
        # If not, attempt single point calculation
        # If none, update target found bool accordingly
        if len(imgpoints) >= 2:
            center_ret, angle, distance = projection_math.leg_calculation(imgpoints, 
                                                            dampen=dampen_weight, 
                                                            prev=(angle, distance), 
                                                            draw_target=draw_frame, 
                                                            visualizer=rw_vis)

        elif len(imgpoints) == 1:
            center_ret, angle, distance = projection_math.single_point(imgpoints[0], draw_target=draw_frame)
        else:
            center_ret = False


        # If a target center is not succesfully returned, set angle & distance to 0 and tell SmartDashboard no targets were found
        if not center_ret:
            angle = 0
            distance = 0


        # Update NT
        table.putNumber("IsTargetFoundInFrame", int(center_ret))
        table.putNumber("RelativeDistanceToHub", distance)
        table.putNumber("RelativeAngleToHub", angle)

        # Calculate time elapsed since last image push
        time_elapsed = time.time() - prev_time
        
        # If the time elapsed since the last image push equals or exceeds the time of one frame per second
        # then push a new frame and reset the time of the last image push
        if time_elapsed >= 1/fps:
            prev_time = time.time()
        
            # Set the output of the camera server to the selected camera
            # 0 - "clean" image
            # 1 - binary mask w/ "clean" underlay
            # 2 - Real-world visualizer
            if table.getNumber("CameraSelection", 0) == 0:
                # Center image lines
                cv2.line(draw_frame, (320//2, 0), (320//2,480//2), (255,255,255), thickness=2)
                cv2.line(draw_frame, (0,240), (640//2, 240//2), (255,255,255), thickness=2)

                outputStream.putFrame(draw_frame)

            elif table.getNumber("CameraSelection", 0) == 1:
                # Convert the mixed light & turret mask to BGR
                full_mask = cv2.cvtColor(full_mask, cv2.COLOR_GRAY2BGR)
                
                # Size the combined mask down
                push_frame = res_img(full_mask)

                # Define width and height of sized down combined mask
                h, w, _ = push_frame.shape

                # Draw center vertical line
                cv2.line(push_frame, (w//2,0), (w//2,h), (0,0,255))

                outputStream.putFrame(push_frame)

            elif table.getNumber("CameraSelection", 0) == 2:
                frame = rw_vis.retrieve_img()
                outputStream.putFrame(frame)


    # Release capture object
    cap.release()

if __name__ == "__main__":
    main()
