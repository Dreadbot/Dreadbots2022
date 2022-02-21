import cv2
import numpy as np
import math
from networktables import NetworkTables
import threading
from cscore import CameraServer


def main():
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

    flength = 667  # We know the focal length of the camera to be 544
    camOffsetDegree = 0  # TO FIND IN THE MOMENT
    targetHeight = 20  # TO FIND IN THE MOMENT

    if table is not None:
        table.putNumber("TargetHLowerValue", 50)
        table.putNumber("TargetHUpperValue", 90)
        table.putNumber("TargetLLowerValue", 20)
        table.putNumber("TargetLUpperValue", 100)
        table.putNumber("TargetSLowerValue", 200)
        table.putNumber("TargetSUpperValue", 255)

    h = [50, 90]
    l = [20, 100]
    s = [200, 255]

    # Read until video is completed
    while(cap.isOpened()):
        XconPositionList = []
        YconPositionList = []

        # Capture frame-by-frame
        ret, inputImg = cap.read()

        if not ret:
            break

        # Sets the img_h to the vertical pixels of the camera
        img_h = inputImg.shape[0]
        # Sets the img_w to the horizontal pixels of the camera
        img_w = inputImg.shape[1]
        cx = int(img_w/2)  # The X position of the center of the IMAGE
        cy = int(img_h/2)  # The Y position of the cetner of the IMAGE

        # applies many filters those filters beiiing...
        # Converting the input to HLS color
        hlsImg = cv2.cvtColor(inputImg, cv2.COLOR_BGR2HLS)
        # - find the HSL values for different environments/lights in the README.md

        if table is not None:
            h = (table.getNumber("TargetHLowerValue", 0),
                 table.getNumber("TargetHUpperValue", 255))
            l = (table.getNumber("TargetLLowerValue", 0),
                 table.getNumber("TargetLUpperValue", 255))
            s = (table.getNumber("TargetSLowerValue", 0),
                 table.getNumber("TargetSUpperValue", 255))

        # Blurs the HLS image a bit to make it easier to work with
        blurImg = cv2.GaussianBlur(hlsImg, (7, 7), 0)
        # Checks all pixels and changes the ones outside the range to black and the ones in to white
        maskImg = cv2.inRange(blurImg, (h[0], l[0], s[0]), (h[1], l[1], s[1]))
        # Dilates out all the found sections so we can get them more solid
        dilateImg = cv2.dilate(maskImg, (7, 7), 20)
        # Finds then adds the contours over the original image
        contours, hierarchy = cv2.findContours(
            image=dilateImg, mode=cv2.RETR_TREE, method=cv2.CHAIN_APPROX_NONE)
        contourImg = cv2.drawContours(image=inputImg, contours=contours,
                                      contourIdx=-1, color=(0, 0, 255), thickness=2, lineType=cv2.LINE_AA)

    # Add the contours over the inputImg to make the final pushed image
        imgToPush = cv2.add(inputImg, np.array([50.]))

        if len(contours) > 0:
            for c in contours:
                # Creates and then seperates the bounds of positioning and width and height of the camera input
                bounds = cv2.boundingRect(c)
                x, y, w, h = bounds
                # Sets lots of variables
                conCX = w/2 + x  # The X position of the center of the CONTOUR
                conCY = h/2 + y  # The Y position of the center of the CONTOUR
                # Checks the width (w) and height (h) of every contour in the frame and only puts the targets over the ones in the range
                if 20 < w < 80 and 10 < h < 60:
                    # Appends the center of the contour to the position lists
                    XconPositionList.append(conCX)
                    YconPositionList.append(conCY)

            targetFound = len(XconPositionList) != 0 and len(
                YconPositionList) != 0

            if table is not None:
                table.putBoolean("TargetFoundInFrame", targetFound)

            if targetFound:
                # Averages the X and Y positions from the X and Y conposition list variables, then sets them to avgXpos and avgYpos
                avgXpos = sum(XconPositionList) / len(XconPositionList)
                avgYpos = sum(YconPositionList) / len(YconPositionList)

                # target = _drawTargets(
                #     x, y, w, h, (0, 255, 0), (255, 0, 0), (255, 255, 255))

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

                table.putNumber("RelativeDistanceToHub",
                                (distance / 39.37))
                table.putNumber("RelativeAngleToHub", fin_angle_hori)

        if cs is not None:
            camId = table.getNumber("CurrentCameraNumber", 0)

            if camId == 2:
                outputStream.putFrame(inputImg)
            elif camId == 3:
                outputStream.putFrame(dilateImg)

        # if len(contours) == 0:
        #     avgXpos = -1
        #     avgYpos = -1
        #     XconPositionList.clear()
        #     YconPositionList.clear()

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
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # When everything done, release the video capture object and close any open windows that show video feeds
    cap.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    main()
