import numpy as np
import projection_math
import random
import threading
import cv2
from math import sin, cos, degrees, radians


imgpts = [(396, -67), (398, -29), (397, -1), (398, 24), (392, 59), (402, 93), (391, 142)] # USED TO CALCULATE "X"

real = [152,176,200,224,248,272,296]

learning_rate = 0.001
epoch = 10000

yaw   = 8
pitch = 26
roll  = 0

def r(yi):
    global yaw
    global pitch
    global roll

    output = (sin(yaw)*cos(roll) + cos(yaw)*sin(pitch)*sin(roll) + cos(yaw)**2 - sin(roll)*sin(yaw) + cos(yaw)*sin(pitch)*cos(roll))*yi

    return(output)

def update_angles():
    global yaw
    global pitch
    global roll

    n = len(real)

    d_yaw = 0.0
    d_pitch = 0.0
    d_roll = 0.0

    R = projection_math.rotation_matrix(degrees(yaw), degrees(pitch), degrees(roll))

    for i in range(n):
        u,v = imgpts[i]
        _, xi, _ = projection_math.similar_triangles_calculation(u, v, R=projection_math.rotation_matrix(0, 0, 0))
        yi = real[i]

        """
        Note:

        Hey future cole here's your notes leaving off. I think I need to step back and reapproach this because I've got like 16 different methods and approaches going at once. Why
        am I re-rotating the unrotated vector? I should need to rotate from the projection right?? I shouldn't be able to do that without the yeah idk im tired

        check derivatives, rewrite logic, this is dope just finish it
        """

        # print(f"--xi[{xi}] yi[{yi}]--")

        # d_yaw   += 2*(yi-r(xi)) * -xi*( cos(yaw)*cos(roll) - sin(yaw)*sin(pitch)*sin(roll) - sin(2*yaw) - sin(roll)*cos(yaw) - sin(yaw)*sin(pitch)*cos(roll))
        # d_pitch += 2*(yi-r(xi)) * -xi*( cos(yaw)*cos(pitch)*sin(roll) + cos(yaw)*cos(pitch)*cos(roll))
        # d_roll  += 2*(yi-r(xi)) * -xi*(-sin(yaw)*sin(roll) + cos(yaw)*sin(pitch)*cos(roll) - cos(roll)*sin(yaw) - cos(yaw)*sin(pitch)*sin(roll))

        d_yaw   += 2*(yi-r(xi)) * -xi*(cos(roll)*cos(yaw) - sin(roll)*cos(yaw) - 2*sin(roll)*sin(pitch)*sin(yaw) - sin(2*yaw))
        d_pitch += 2*(yi-r(xi)) * -xi*(2*cos(yaw)*sin(roll)*cos(pitch))
        d_roll  += 2*(yi-r(xi)) * -xi*(2*cos(yaw)*cos(roll)*sin(pitch) - sin(yaw)*sin(roll) - sin(yaw)*cos(roll))

        
        # print(f"--d_yaw[{d_yaw}] d_pitch[{d_pitch}] d_roll[{d_roll}]--")
    yaw -= (d_yaw/float(n)) * learning_rate
    pitch -= (d_pitch/float(n)) * learning_rate
    roll -= (d_roll/float(n)) * learning_rate


def start_from_initial(yaw, pitch, roll):
    yaw   = radians(0.1)
    pitch = radians(0.1)
    roll  = radians(0.1)
    for i in range(epoch):
        update_angles()
        print(f"Loop #{i} | yaw[{yaw}] pitch[{pitch}] roll[{roll}] ")
    print(f"Complete!\nYaw: {degrees(yaw)}\nPitch: {degrees(pitch)}\nRoll: {degrees(roll)}\n\nLearning Rate: {learning_rate}\nEpochs: {epoch}")

start_from_initial(0, 0, 0)