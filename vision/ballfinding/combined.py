import cv2
import util
import circularity
import hough
import threading
from networktables import NetworkTables


def main():
    rangeName = input("Range: ")

    if input("Connect to NT?: ") == "y":
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

    util.setupDefaultSliderWindow("hsv", "Trackbars", rangeName)

    vc = cv2.VideoCapture(1)
    vc.set(cv2.CAP_PROP_EXPOSURE, -4)

    while True:
        ret, frame = vc.read()

        if not ret:
            break

        hL, sL, vL, hU, sU, vU, erode, dilate, blur, minArea, circ = util.getSliderValues(
            "hsv", "Trackbars")

        lower = (hL, sL, vL)
        upper = (hU, sU, vU)
        minCirc = circ / 100

        mask = util.getMask(frame, lower, upper, erode, dilate, blur)

        cv2.imshow("Binary", mask)

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
                c = ()
                for i in range(3):
                    c += ((circle[i] + nextCircle[i]) / 2,)

                filteredCircles.append(c)
                cv2.circle(frame, (int(c[0]), int(c[1])),
                           int(c[2]), (255, 255, 0), 2)

        dX = -1
        dZ = -1

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

        cv2.imshow("Frame", frame)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            util.updateLiveRange(rangeName, (hL, sL, vL), (hU, sU, vU))

            util.setAllManipulation(erode, dilate, blur, minArea, circ)
            break

    vc.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
