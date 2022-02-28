#!/usr/bin/python3
import cv2
import util
import circularity
import hough
import threading
import argparse
from networktables import NetworkTables
from cscore import CameraServer


def main():
    argparser = argparse.ArgumentParser(
        description="Tracks balls using circularity and hough circles.")
    argparser.add_argument(
        '--no-tables', action='store_false', dest='ntenabled')
    argparser.add_argument(
        '--no-server', action='store_false', dest='csenabled')
    argparser.add_argument('-r', '--range', action='store', dest='colorrange')
    argparser.add_argument('-p', '--data-path',
                           action='store', dest='datapath')
    argparser.add_argument(
        '-v', '--visual', action='store_true', dest='visualmode')
    args = argparser.parse_args()

    if args.datapath is not None:
        util.setDataDirectory(args.datapath)

    if args.ntenabled:
        cond = threading.Condition()
        notified = [False]

        def connectionListener(connected, info):
            print(info, '; Connected=%s' % connected)
            with cond:
                notified[0] = True
                cond.notify()

        NetworkTables.initialize(server=util.server)
        NetworkTables.addConnectionListener(
            connectionListener, immediateNotify=True)

        with cond:
            print("Waiting")
            if not notified[0]:
                cond.wait()

        print("Connected!")

        table = NetworkTables.getTable('SmartDashboard')
    else:
        table = None

        # util.setupDefaultSliderWindow("hsv", "Trackbars", rangeName)
    if args.colorrange is not None:
        cRange = args.colorrange

        if not util.isLiveRange(cRange):
            util.updateLiveRange(cRange, (0, 0, 0), (255, 255, 255))

        range = util.getLiveRange(cRange)
        lower = range["lower"]
        upper = range["upper"]

        for c in "HSV":
            i = "HSV".index(c)

            table.putNumber(f"{c}LowerValue", lower[i])
            table.putNumber(f"{c}UpperValue", upper[i])

    if args.csenabled and table is not None:
        cs = CameraServer.getInstance()
        cs.enableLogging()

        outputStream = cs.putVideo("Source", 640, 480)
        table.putNumber("CurrentCameraNumber", 0)
    else:
        cs = None

    vc = cv2.VideoCapture(0)
    vc.set(cv2.CAP_PROP_EXPOSURE, -4)

    while True:
        ret, frame = vc.read()

        if not ret:
            break

        if table is None:
            hL, sL, vL, hU, sU, vU, erode, dilate, blur, minArea, circ = util.getSliderValues(
                "hsv", "Trackbars")
        else:
            hL = table.getNumber("HLowerValue", 0)
            hU = table.getNumber("HUpperValue", 255)

            sL = table.getNumber("SLowerValue", 0)
            sU = table.getNumber("SUpperValue", 255)

            vL = table.getNumber("VLowerValue", 0)
            vU = table.getNumber("VUpperValue", 255)

            manip = util.getManipulation()
            erode = manip["erode"]
            dilate = manip["dilate"]
            blur = manip["blur"]
            minArea = manip["area"]
            circ = manip["circ"]

        lower = (hL, sL, vL)
        upper = (hU, sU, vU)
        minCirc = circ / 100

        mask = util.getMask(frame, lower, upper, erode, dilate, blur)

        circle = circularity.getBall(mask, minCirc, minArea)
        h = hough.getBall(cv2.blur(mask, (blur + 8, blur + 8), 0))
        circles = []
        if circle is not None:
            circles.append((circle[0], circle[1], circle[2]))

        if h is not None:
            circles.append((h[0], h[1], h[2]))

        # print(circles)
        radiusError = 10  # In Pixels
        xyError = 10  # In Pixels

        filteredCircles = []

        for circle in circles:
            i = circles.index(circle)
            if i + 1 == len(circles):
                continue

            nextCircle = circles[i + 1]

            if abs(circle[2] - nextCircle[2]) < radiusError \
                    and abs(circle[0] - nextCircle[0]) < xyError \
                    and abs(circle[1] - nextCircle[1]) < xyError:

                avgX = (circle[0] + nextCircle[0]) / 2
                avgY = (circle[1] + nextCircle[1]) / 2
                avgR = (circle[2] + nextCircle[2]) / 2
                c = (avgX, avgY, avgR)

                filteredCircles.append(c)
                cv2.circle(frame, (int(c[0]), int(c[1])),
                           int(c[2]), (255, 255, 0), 2)

        if table is not None:
            table.putNumber("TotalBallsFoundInFrame", len(filteredCircles))

        if len(filteredCircles) > 0:
            bestCircle = filteredCircles[0]
            for betterC in filteredCircles:
                if betterC[2] > bestCircle[2]:
                    bestCircle = betterC

            dX, dZ, distance, angle = util.getDistance(
                frame, bestCircle[0], bestCircle[2], util.focalLength, util.ballDiameter)

            if table is not None:
                table.putNumber("RelativeDistanceToBallX", dX)
                table.putNumber("RelativeDistanceToBallZ", dZ)
                table.putNumber("RelativeAngleToBall", angle)

        if cs is not None:
            camId = table.getNumber("CurrentCameraNumber", 0)

            if camId == 0:
                outputStream.putFrame(frame)
            elif camId == 1:
                outputStream.putFrame(mask)
            else:
                table.putNumber("CurrentCameraNumber", 0)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            if args.colorrange is not None:
                util.updateLiveRange(cRange, (hL, sL, vL), (hU, sU, vU))

                util.setAllManipulation(erode, dilate, blur, minArea, circ)

            break

    vc.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
