from cscore import CameraServer
from networktables import NetworkTables
import numpy as np
import threading
import dreadbot_fisheye as df
import ballfinding
import json
import os
import math

# https://www.geeksforgeeks.org/python-program-for-quicksort/
# I did not feel like writing this out myself, so I just modified it

def partitionKeyed(arr, key, low, high):
        i = (low-1)         # index of smaller element
        pivot = arr[high][key]     # pivot
    
        for j in range(low, high):
    
            # If current element is smaller than or
            # equal to pivot
            if arr[j][key] <= pivot:
    
                # increment index of smaller element
                i = i+1
                arr[i], arr[j] = arr[j], arr[i]
    
        arr[i+1], arr[high] = arr[high], arr[i+1]
        return (i+1)

# arr[] --> Array of balls to be sorted,
# low  --> Starting index,
# high  --> Ending index
 
# Function to do Quick sort
def quickSortKeyed(arr, key, low=None, high=None):
    l = len(arr)
    if low is None: low = 0
    if high is None: high = l - 1

     
    if l == 1:
        return arr
    if low < high:
 
        # pi is partitioning index, arr[p] is now
        # at right place
        pi = partitionKeyed(arr, key, low, high)
 
        # Separately sort elements before
        # partition and after partition
        quickSortKeyed(arr, key, low, pi-1)
        quickSortKeyed(arr, key, pi+1, high)


cam_distance = 1 # Inches
# TODO : calculate NavX vector
navx = [
    0,
    0,
    0,
] # Vector between the NavX and the top camera
# TODO : calculate distance off the ground of the NavX
dng = 1 # Distance off the ground to the NavX
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
        foundBalls = [[],[]]

        for cam_num, camera in enumerate(cameras):
            ret, frame = camera.retrieve_undistorted_img()

            if not ret:
                fisheyeReadError = True
                break

            rawFoundBalls = ballfinding.find_balls_in_frame(
                frame, lower, upper, erode, dilate, blur, minCircP / 100, minArea, radiusError, xyError)

            for ball in rawFoundBalls:
                angleX, angleY = camera.calculate_angle(
                    ball[0], ball[1])

                ball0 = {
                    "x": ball[0],
                    "y": ball[1],
                    "radius": ball[2],
                    "yaw": angleX,
                    "pitch": angleY
                }

                foundBalls[cam_num].append(ball0)
    
        if fisheyeReadError:
            print("Error getting feed from fisheyes.")
            break

        if len(foundBalls) != 2:
            continue

        any_valid_balls = False

        # QuickSort because fast
        quickSortKeyed(foundBalls[0], 'x')
        quickSortKeyed(foundBalls[1], 'x')
        
        for ball1, ball2 in zip(foundBalls[0], foundBalls[1]):
            if abs(ball1['x'] - ball2['x']) > stereoXError:
                continue

            a = (ball1['yaw'] + ball2['yaw']) / 2 # Average the yaws
            t = distance_vec(ball1['pitch'], ball2['pitch'], a)
            
            a = math.tan(t[2]/t[0]) # t[0] = x, t[2] = z
            d = math.sqrt((t[0]**2) + (t[2]**2))

            bg = (dng + (d*math.sin(a))) - br
            
            if -ground_threshold < bg < ground_threshold:
                table.putNumber("RelativeDistanceToBallX", t[0]) # t[0] = x
                table.putNumber("RelativeDistanceToBallY", t[1]) # t[1] = y
                any_valid_balls = True
                return
        
        table.putBoolean("FoundBallInFrame", any_valid_balls)

    for camera in cameras:
        camera.unload()


if __name__ == "__main__":
    main()
