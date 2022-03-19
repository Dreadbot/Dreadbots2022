import cv2
import os
import subprocess
import argparse
from cscore import CameraServer
import traceback

def main():
    argparser = argparse.ArgumentParser(description="Uh oh stinky")
    argparser.add_argument('-f', '--feed', action='store_true', dest='feedenabled')
    args = argparser.parse_args()

    if args.feedenabled:
        cs = CameraServer.getInstance()
        cs.enableLogging()

        outputStream = cs.putVideo('Source', 640, 480)

    cams = []

    for i in range(10):
        try:
            cam = cv2.VideoCapture(i)
            serial = os.popen(f'udevadm info --name=/dev/video{i} | grep ID_SERIAL= | cut -d "=" -f 2').read().replace("\n", "")
            camStr = f"Cam {i} ({cam.getBackendName()}): {serial}"
            print(camStr)

            while args.feedenabled:
                ret, frame = cam.read()

                if not ret: break

                outputStream.putFrame(frame)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
            
            cams.append(camStr)
            cam.release()
        except:
            continue

    print("\nCameras found at: ")
    for c in cams:
        print(c)

if __name__ == "__main__":
    main()



