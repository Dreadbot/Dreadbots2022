import projection_math as pm
import cv2
from cscore import CameraServer
import threading
from networktables import NetworkTables


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

    cs = CameraServer.getInstance()
    cs.enableLogging()

    outputStream = cs.putVideo("Source", 640, 480)

    pm.calibrate_intrinsic(0, outputStream, table)
    

if __name__ == "__main__":
    main()


