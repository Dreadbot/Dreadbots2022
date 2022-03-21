from cscore import CameraServer
from networktables import NetworkTables
import dreadbot_fisheye as df
import cv2
import imutils
import panning
import argparse
import threading
import numpy as np

def combine_imgs(img1, img2):
    return np.concatenate((img1, np.zeros([2, img1.shape[1], 3]), img2), axis=1)

def main(fisheye1: df.Fisheye, fisheye2: df.Fisheye):
    argparser = argparse.ArgumentParser(name="DriverCams", description="Pushes driver cams to CameraServer")
    argparser.add_argument('-s', '--scale', action='store', type=int, default=2, dest='scale', help="Scale factor for the image being pushed")
    args = argparser.parse_args()
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

    sc = args.scale # Scale factor
    
    img1, img2 = fisheye2.retrieve_undistorted_img(), fisheye1.retrieve_undistorted_img()
    combinedimg = combine_imgs(img1, img2)
    w, h = combinedimg.shape[0] // sc, combinedimg.shape[1] // sc
    outputStream = cs.putVideo("Source", w, h)
    
    while True:
        img = combine_imgs(fisheye1.retrieve_undistorted_img(), fisheye2.retrieve_undistorted_img())
        outputStream.putFrame(cv2.resize(img, (w, h)))
        # outputStream.putFrame(img)
        

if __name__ == "__main__":
    fisheye1 = df.Fisheye(2,0)
    fisheye2 = df.Fisheye(0,0)
    try:
        main(fisheye1, fisheye2)
    except KeyboardInterrupt:
        fisheye1.unload()
        fisheye2.unload()

