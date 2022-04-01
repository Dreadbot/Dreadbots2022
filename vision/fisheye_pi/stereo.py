from cscore import CameraServer
from networktables import NetworkTables
import numpy as np
import threading
import dreadbot_fisheye as df
import ballfinding
import json
import os
import math

cam_distance = 1 # Inches
# TODO : calculate NavX vector
navx = [
    0,
    0,
    0,
] # Vector between the NavX and the top camera
dng = 1 # Distance off the ground to the navigator
br = 5 # 5", the radius of the ball
ground_threshold = 10

def distance_vec(a1, a2, hangle):
    #   a1
    #    |\
    #    | \ s2
    #    |  \ a3
    # s3 |  /
    #    | / s1
    #    |/
    #   a2
    # https://www.desmos.com/calculator/lwjxsbalmm
    a3 = 180 - a1 - a2
    s3 = cam_distance
    s2 = s3 * ( math.sin(a2) / math.sin(a3) )
    ar = a1 - 90
    vfy = (s2*math.sin(ar))
    vfx = (s2*math.cos(ar))
    d = math.sqrt((vfy**2) + (vfx**2))
    b = [
        d * math.cos(a1),
        d * math.sin(a1),
        (d * math.cos(a1)) * math.tan(hangle),
    ]

    # ⎛n ⎞   ⎛f ⎞      
    # ⎜ x⎟   ⎜ x⎟      
    # ⎜  ⎟   ⎜  ⎟   ⎛x⎞
    # ⎜n ⎟   ⎜f ⎟   ⎜ ⎟
    # ⎜ y⎟ + ⎜ y⎟ = ⎜y⎟
    # ⎜  ⎟   ⎜  ⎟   ⎜ ⎟
    # ⎜n ⎟   ⎜f ⎟   ⎝z⎠
    # ⎝ z⎠   ⎝ z⎠      
    # Where n is the vector from the navx to the fisheye
    # and f is the vector from the fisheye to the ball

    return np.add(navx, b)
    

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

                ball0 = {
                    "x": foundBall[0],
                    "y": foundBall[1],
                    "radius": foundBall[2],
                    "yaw": angleX,
                    "pitch": angleY
                }

                foundBalls.append(ball0)

        if fisheyeReadError:
            print("Error getting feed from fisheyes.")
            break

        if len(foundBalls) != 2:
            continue

        ball1, ball2 = foundBalls[0], foundBalls[1]

        if abs(ball1.x - ball2.x) > stereoXError:
            continue

        a = (ball1.yaw + ball2.yaw) / 2 # Average the yaws
        t = distance_vec(ball1.pitch, ball2.pitch, a)

        # TODO : ensure that the ball is on the ground before we track it
        # This is really easy to do I just don't have the mental capacity rn
        
        a = math.tan(t[2]/t[0]) # t[0] = x, t[2] = z
        d = math.sqrt((t[0]**2) + (t[2]**2))

        bg = (dng + (d*math.sin(a))) - br
        is_on_ground = -ground_threshold < bg < ground_threshold
        
        table.putNumber("RelativeDistanceToBallX", t[0]) # t[0] = x
        table.putNumber("RelativeDistanceToBallY", t[1]) # t[1] = y
        table.putBoolean("BallIsOnGround", is_on_ground)

    for camera in cameras:
        camera.unload()


if __name__ == "__main__":
    main()
