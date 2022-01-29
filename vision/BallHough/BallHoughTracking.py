import math
import time
import cv2
from cv2 import threshold
import numpy as np
cv = cv2
pi = 3.14159265358979323846264327950288419716939
r = 0
import argparse
max_value = 255
max_value_H = 360//2
low_H = 0 #Blue 95 
low_S = 108 #Blue 150 
low_V = 0 #Blue 106 
high_H = 12 #Blue 125 
high_S =  255 #Blue 218 
high_V =  255 #Blue 255 
window_capture_name = 'Video Capture'
window_detection_name = 'Object Detection'
low_H_name = 'Low H'
low_S_name = 'Low S'
low_V_name = 'Low V'
high_H_name = 'High H'
high_S_name = 'High S'
high_V_name = 'High V'


def on_low_H_thresh_trackbar(val):
    global low_H
    global high_H
    low_H = val
    low_H = min(high_H-1, low_H)
    cv.setTrackbarPos(low_H_name, window_detection_name, low_H)
def on_high_H_thresh_trackbar(val):
    global low_H
    global high_H
    high_H = val
    high_H = max(high_H, low_H+1)
    cv.setTrackbarPos(high_H_name, window_detection_name, high_H)
def on_low_S_thresh_trackbar(val):
    global low_S
    global high_S
    low_S = val
    low_S = min(high_S-1, low_S)
    cv.setTrackbarPos(low_S_name, window_detection_name, low_S)
def on_high_S_thresh_trackbar(val):
    global low_S
    global high_S
    high_S = val
    high_S = max(high_S, low_S+1)
    cv.setTrackbarPos(high_S_name, window_detection_name, high_S)
def on_low_V_thresh_trackbar(val):
    global low_V
    global high_V
    low_V = val
    low_V = min(high_V-1, low_V)
    cv.setTrackbarPos(low_V_name, window_detection_name, low_V)
def on_high_V_thresh_trackbar(val):
    global low_V
    global high_V
    high_V = val
    high_V = max(high_V, low_V+1)
    cv.setTrackbarPos(high_V_name, window_detection_name, high_V)
cv.namedWindow(window_detection_name)
cv.createTrackbar(low_H_name, window_detection_name , low_H, max_value_H, on_low_H_thresh_trackbar)
cv.createTrackbar(high_H_name, window_detection_name , high_H, max_value_H, on_high_H_thresh_trackbar)
cv.createTrackbar(low_S_name, window_detection_name , low_S, max_value, on_low_S_thresh_trackbar)
cv.createTrackbar(high_S_name, window_detection_name , high_S, max_value, on_high_S_thresh_trackbar)
cv.createTrackbar(low_V_name, window_detection_name , low_V, max_value, on_low_V_thresh_trackbar)
cv.createTrackbar(high_V_name, window_detection_name , high_V, max_value, on_high_V_thresh_trackbar)

camera = cv2.VideoCapture(0)
camera.set(cv2.CAP_PROP_EXPOSURE, -4)



while True:
    ret,frame = camera.read()


    if not ret:
        break

    
    
    #TIMING DIAL FOR HOW MANY UPDATES PER SECOND
    
    #time.sleep(0.25) 
      

    #Gaussian = cv2.GaussianBlur(frame, (33, 33),0)
    #grey_image = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    half = cv2.resize(frame, (0, 0), fx = 0.6, fy = 0.6)
    
    hsvImage = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    frameThreshold = cv.inRange(hsvImage, (low_H, low_S, low_V), (high_H, high_S, high_V))
   
    Gaussian = cv2.GaussianBlur(frameThreshold, (51, 51), 0)
    
    kernel = np.ones((5,5), np.uint8)
    img_erosion = cv2.erode(Gaussian, kernel, iterations=5)
    img_dilation = cv2.dilate(img_erosion, kernel, iterations=5)
    
    half2 = cv2.resize(img_dilation, (0, 0), fx = 0.5, fy = 0.5)

    edged = cv2.Canny(img_dilation, 30, 200)
    contours, hierarchy = cv2.findContours(edged, 
    cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cv2.drawContours(img_dilation, contours, -1, (0, 255, 0), 3)
   
    # Apply Hough transform on the blurred image.
    detected_circles = cv2.HoughCircles(half2, 
                   cv2.HOUGH_GRADIENT, 1, 20, param1 = 50,
               param2 = 30, minRadius = 10, maxRadius = 1000)
  
    # Draw circles that are detected.
    if detected_circles is not None:
  
         #Convert the circle parameters a, b and r to integers.
        detected_circles = np.uint16(np.around(detected_circles))
  
        for pt in detected_circles[0, :]:
            a, b, r = pt[0], pt[1], pt[2]
  
        # Draw the circumference of the circle.
        cv2.circle(half2, (a, b), r, (255, 0, 0), 2)
  
        # Draw a small circle (of radius 1) to show the center.
        cv2.circle(half2, (a, b), 1, (0, 0, 255), 3)
        #cv2.imshow("Detected Circle", half2)

        #calibration
        #cv2.inRange(img_dilation, (61, 87, 26), (73, 255, 255))

    cv2.imshow("Raw", half)
    cv2.imshow("HSV Converted", half2)

    
    
    





    #Distance Find + MEASUREMENT DISPLAY
       



    areaat1meter = 6939
    area = r**2*pi
    #print(str(area))
    yaxis= .13335 #the height difference betweent he camera lens and the top of circle
    hyp = math.sqrt((1/(area/areaat1meter)))
    #print("hyp = "+ str(hyp))

    distance = math.sqrt(((hyp**2)-(yaxis**2)))



    #AREA PRINT

    img2 = np.zeros((112, 512, 3), np.uint8)

       

    font                   = cv2.FONT_HERSHEY_SIMPLEX
    topLeftCornerOfText = (10,30)
    fontScale              = 1
    fontColor              = (255,255,255)
    thickness              = 1
    lineType               = 2

    cv2.putText(img2,str(area), 
        topLeftCornerOfText, 
        font, 
        fontScale,
        fontColor,
        thickness,
        lineType)

    cv2.imshow("Area",img2) 


    #DISTANCE PRINT

    img = np.zeros((112, 512, 3), np.uint8)

       

    font                   = cv2.FONT_HERSHEY_SIMPLEX
    topLeftCornerOfText = (10,30)
    fontScale              = 1
    fontColor              = (255,255,255)
    thickness              = 1
    lineType               = 2

    cv2.putText(img,str(distance), 
        topLeftCornerOfText, 
        font, 
        fontScale,
        fontColor,
        thickness,
        lineType)

    #Display the image
    cv2.imshow("Distance",img)   

    print("low hue: " + str(low_H) + "\nlow saturation: " + str(low_S) + "\nlow value" + "\nhigh hue: " + str(high_H) + "\nhigh saturation: " + str(high_S) + "\nhigh value: " + str(high_V))

    if cv2.waitKey(1) & 0xFF == ord("q"):
        print("low hue: " + str(low_H) + "\nlow saturation: " + str(low_S) + "\nlow value" + "\nhigh hue: " + str(high_H) + "\nhigh saturation: " + str(high_S) + "\nhigh value: " + str(high_V))
        
        break



camera.release()
cv2.destroyAllWindows()

