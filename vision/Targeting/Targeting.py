import cv2
import numpy as np
import math
from networktables import NetworkTables
import threading
from cscore import CameraServer

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

if table is not None:
    cs = CameraServer.getInstance()
    cs.enableLogging()

    outputStream = cs.putVideo("Source", 620, 480)
else:
    cs = None

def _drawTargets(x, y, w, h, rectangleColor, circleColor, lineColor):
    cv2.rectangle(imgToPush, (x, y), (x+w, y+h), rectangleColor, 3)
    cv2.circle(imgToPush, (int(x+(w/2)), int(y+(h/2))), 5, (255, 0, 0))

    target = [int(x+(w/2)), int(y+(h/2))]

    cv2.line(imgToPush, (target[0]-10, target[1]),
             (target[0]+10, target[1]), (255, 255, 255))
    cv2.line(imgToPush, (target[0], target[1]-10),
             (target[0], target[1]+10), (255, 255, 255))
    return target


# Create a VideoCaqpture object and read from input file
cap = cv2.VideoCapture(0)
# Create varibles for trackbars
high_H, high_S, high_L, low_H, low_S, low_L = 0, 0, 0, 0, 0, 0
upper_height, lower_height, upper_width, lower_width = 0, 0, 0, 0
HIGH_VAL_HSL = 255
HIGH_H = 360 // 2
HIGH_VAL_BOUNDING_BOX = 100
# Set the exposure of the camera to help with finding the target
cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
cap.set(cv2.CAP_PROP_EXPOSURE, -15)
# create function for trackbars


def ontrackbar(val):
    pass

# Add window for trackbars and create trackbars


# Check if camera opened successfully
if (cap.isOpened() == False):
    print("Error opening video  file")

found_contours = 0

# Read until video is completed
while(cap.isOpened()):
    XconPositionList = []
    YconPositionList = []
    found_contours = 0
    # Capture frame-by-frame
    ret, inputImg = cap.read()
    if ret == True:
        # applies many filters those filters beiiing...
        # Converting the input to HLS color
        hlsImg = cv2.cvtColor(inputImg, cv2.COLOR_BGR2HLS)
        # - find the HSL values for different environments/lights in the README.md
        h = [50, 90]
        s = [20, 100]
        l = [200, 255]
        # Blurs the HLS image a bit to make it easier to work with
        blurImg = cv2.GaussianBlur(hlsImg, (7, 7), 0)
        # Checks all pixels and changes the ones outside the range to black and the ones in to white
        maskImg = cv2.inRange(blurImg, (h[0], s[0], l[0]), (h[1], s[1], l[1]))
        # Dilates out all the found sections so we can get them more solid
        dilateImg = cv2.dilate(maskImg, (7, 7), 20)
        # Finds then adds the contours over the original image
        contours, hierarchy = cv2.findContours(
            image=dilateImg, mode=cv2.RETR_TREE, method=cv2.CHAIN_APPROX_NONE)
        contourImg = cv2.drawContours(image=inputImg, contours=contours,
                                      contourIdx=-1, color=(0, 0, 255), thickness=2, lineType=cv2.LINE_AA)

# Add the contours over the inputImg to make the final pushed image
        imgToPush = cv2.add(inputImg, np.array([50.]))

# Loops through and adds the targets over the original image
        loopCounterCon = 0
        for c in contours:

            # Creates and then seperates the bounds of positioning and width and height of the camera input
            bounds = cv2.boundingRect(c)
            x, y, w, h = bounds
            # Sets lots of variables
            # Sets the img_h to the vertical pixels of the camera
            img_h = inputImg.shape[0]
            # Sets the img_w to the horizontal pixels of the camera
            img_w = inputImg.shape[1]
            flength = 544  # We know the focal length of the camera to be 544
            camOffsetDegree = 0  # TO FIND IN THE MOMENT
            targetHeight = 20  # TO FIND IN THE MOMENT
            cx = int(img_w/2)  # The X position of the center of the IMAGE
            cy = int(img_h/2)  # The Y position of the cetner of the IMAGE
            conCX = w/2 + x  # The X position of the center of the CONTOUR
            conCY = h/2 + y  # The Y position of the center of the CONTOUR
            distance = 0  # Creating a distance variable
            # Checks the width (w) and height (h) of every contour in the frame and only puts the targets over the ones in the range
            if 20 < w < 80 and 10 < h < 60:
                found_contours += 1
                # Appends the center of the contour to the position lists
                XconPositionList.append(conCX)
                YconPositionList.append(conCY)
                try:
                    # Averages the X and Y positions from the X and Y conposition list variables, then sets them to avgXpos and avgYpos
                    avgXpos = sum(XconPositionList) / len(XconPositionList)
                    avgYpos = sum(YconPositionList) / len(YconPositionList)
                    target = _drawTargets(
                        x, y, w, h, (0, 255, 0), (255, 0, 0), (255, 255, 255))
                    # Calculate angle to turn to
                    fin_angle_hori = (
                        (math.atan((avgXpos - (img_w / 2)) / flength))) * (180 / math.pi)

                    # Calculate vertical angle for distance calculations (LOTS of fancy math cole did that I slightly edited)
                    dy = abs(cy-y)
                    fin_angle_raw_rad = math.atan(dy / flength)
                    fin_angle_deg = math.degrees(
                        fin_angle_raw_rad) + camOffsetDegree
                    fin_angle_rad = math.radians(fin_angle_deg)
                    distance = targetHeight / math.tan(fin_angle_rad)
                except:
                    '''
                    If no target positions are found it spits out an error
                    so to solve that and to let the programming team know we cant find any we just set them to values outside of any range we would be able to find normally
                    In this case the distance and avg positions are set to -1 and the turn angle is set to far outside the 360 degree turn range
                    '''
                    distance = -1
                    fin_angle_hori = 3656
                    avgXpos = -1
                    avgYpos = -1
            loopCounterCon += 1

            table.putNumber("relativeDistanceToHub",
                            (distance / 39.37))
            table.putNumber("relativeAngleToHub", fin_angle_hori)
            
            if cs is not None:
                camId = table.getNumber("CurrentCameraNumber", 0)
                
                if camId == 2:
                    outputStream.putFrame(inputImg)
                elif camId == 3:
                    outputStream.putFrame(imgToPush)

        if len(contours) == 0:
            avgXpos = -1
            avgYpos = -1
            XconPositionList.clear()
            YconPositionList.clear()
        # i am stupid

        # cv2.circle(imgToPush,(int(avgXpos),int(avgYpos)),3,(255,255,0),thickness=-1)
        # cv2.putText(imgToPush, str(fin_angle_hori),(25,400),cv2.FONT_HERSHEY_SIMPLEX,2, (0, 0, 0), 2, cv2.LINE_AA)
        # width = int(imgToPush.shape[1] * 160 / 100)
        # height = int(imgToPush.shape[0] * 160 / 100)
        # dim = (width, height)
        # resized = cv2.resize(imgToPush, dim)
        # if found_contours > 0:
        # 	resized = cv2.putText(resized, str(distance), (25, 80), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 0), 2, cv2.LINE_AA)
        # 	resized = cv2.putText(resized, str(fin_angle_deg), (25, 200), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 0), 2, cv2.LINE_AA)

        # Show final image on your computer with targets shown
        # cv2.imshow('pushed image', imgToPush)

# Press Q on keyboard to  exit
        if cv2.waitKey(1) & 0xFF == ord('\b'):
            break

# Break the loop
    else:
        break

# When everything done, release the video capture object and close any open windows that show video feeds
cap.release()
cv2.destroyAllWindows()
