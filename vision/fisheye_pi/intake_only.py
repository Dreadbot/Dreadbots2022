from cscore import CameraServer
from networktables import NetworkTables
import cv2
import numpy as np
import threading
import dreadbot_fisheye as df
import time

def main():
    cond = threading.Condition()
    notified = [False]

    def connectionListener(connected, info):
        print(info, "; Connected=%s" % connected)
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

    print("Connected")

    table = NetworkTables.getTable('SmartDashboard')

    cs = CameraServer.getInstance()
    cs.enableLogging()
    
    intake = cv2.VideoCapture(0)
    
    intake.set(3, 320)
    intake.set(4, 240)

    _, ref_intake_frame = intake.read()

    ref_intake_h, ref_intake_w, _ = ref_intake_frame.shape

    outputStream = cs.putVideo("Intake", ref_intake_w, ref_intake_h)

    prev_time = 0
    
    fps = 18

    while True:
        #table.putNumber("CameraSelection", 3)

        ret, frame = intake.read()

        if not ret: break

        time_elapsed = time.time() - prev_time

        if time_elapsed >= 1/fps:
            prev_time = time.time()
            if table.getNumber("CameraSelection", 0) == 1:
                outputStream.putFrame(frame)

if __name__ == "__main__":
    main()
