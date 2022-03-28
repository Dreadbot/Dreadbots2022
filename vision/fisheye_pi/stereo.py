from cscore import CameraServer
from networktables import NetworkTables
import threading
import dreadbot_fisheye as df
import ballfinding
import json
import os


def main():
    data = os.path.join(os.getcwd(), "Data")

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

    print("Connected to NT!")

    table = NetworkTables.getTable('SmartDashboard')

    hL = table.getNumber("HLowerValue", 0)
    hU = table.getNumber("HUpperValue", 255)

    sL = table.getNumber("SLowerValue", 0)
    sU = table.getNumber("SUpperValue", 255)

    vL = table.getNumber("VLowerValue", 0)
    vU = table.getNumber("VUpperValue", 255)

    lower = (hL, sL, vL)
    upper = (hU, sU, vU)

    manip = json.load(open(os.path.join(data, "manipulation.json")))
    erode = manip["erode"]
    dilate = manip["dilate"]
    blur = manip["blur"]
    minArea = manip["area"]
    minCircP = manip["circ"]

    cs = CameraServer.getInstance()
    cs.enableLogging()
    outputStream = cs.putVideo("Source", 640, 480)

    fisheyeId0, fisheyeId1, intakeId = 0, 2, -1
    cameras = df.Fisheye(fisheyeId0, 0, -4), df.Fisheye(fisheyeId1, 0, -4)

    # In Pixels
    radiusError = 10
    xyError = 10
    stereoXError = 10

    while True:
        fisheyeReadError = False
        foundBalls = []

        for camera in cameras:
            ret, frame = camera.retrieve_undistorted_img()

            if not ret:
                fisheyeReadError = True
                break

            foundBall = ballfinding.find_ball_in_frame(
                frame, lower, upper, erode, dilate, blur, minCircP / 100, minArea, radiusError, xyError)
            if foundBall is not None:
                angleX, angleY = camera.calculate_angle(
                    foundBall[0], foundBall[1])

                ballO = {
                    "x": foundBall[0],
                    "y": foundBall[1],
                    "radius": foundBall[2],
                    "yaw": angleX,
                    "pitch": angleY
                }

                foundBalls.append(ballO)

        if fisheyeReadError:
            print("Error getting feed from fisheyes.")
            break

        if len(foundBalls) != 2:
            continue

        ball1, ball2 = foundBalls[0], foundBalls[1]

        if abs(ball1.x - ball2.x) > stereoXError:
            continue

    for camera in cameras:
        camera.unload()


if __name__ == "__main__":
    main()
