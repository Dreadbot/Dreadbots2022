import cv2
from cscore import CameraServer

def main():
    cs = CameraServer.getInstance()
    cs.enableLogging()
    outputStream = cs.putVideo('Source', 640, 480)

    cam = cv2.VideoCapture('/dev/video0')

    while True:
        ret, frame = cam.read()

        if not ret: break

        outputStream.putFrame(frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cam.release()


if __name__ == '__main__':
    main()

