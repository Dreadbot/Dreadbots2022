from cscore import CameraServer
from networktables import NetworkTables
import dreadbot_fisheye as df
import cv2
import imutils
import panning
import threading

def main(fisheye1: df.Fisheye, fisheye2: df.Fisheye):
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
    table.putNumber("FisheyePanningPosition", 0)

    cs = CameraServer.getInstance()
    cs.enableLogging()

    ow = 450 # Overlap width
    sc = 2 # Scale factor
    pw = 480
    
    panner = panning.Panner(pw)
    firstblend = df.image_blend(fisheye2.retrieve_undistorted_img(), fisheye1.retrieve_undistorted_img(), ow)
    outputStream = cs.putVideo("Source", panner.width//sc, 480//sc)
    
    while True:
        img = df.image_blend(fisheye1.retrieve_undistorted_img(), fisheye2.retrieve_undistorted_img(), ow)
        panner.position = table.getNumber("FisheyePanningPosition", 0)
        panned_img = panner.get_panned_image(img)
        outputStream.putFrame(cv2.resize(panned_img, (panner.width//sc, 480//sc)))
        # outputStream.putFrame(img)
        

if __name__ == "__main__":
    fisheye1 = df.Fisheye(2,0)
    fisheye2 = df.Fisheye(0,0)
    try:
        main(fisheye1, fisheye2)
    except KeyboardInterrupt:
        fisheye1.unload()
        fisheye2.unload()

