import cv2
from cscore import CameraServer

cs = CameraServer.getInstance()
cs.enableLogging()
outputStream = cs.putVideo("Source", 640, 480)

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

currentCam = table.getNumber("TestSelectionCam", 0)

cap = cv2.VideoCapture(currentCam)

while True:
    camSelection = table.getNumber("TestSelectionCam", 0)

    if camSelection != currentCam:
        currentCam = camSelection
        cap.release()
        cap = cv2.VideoCapture(currentCam)

    ret, frame = cap.read()

    if not ret: 
        print(f"No camera at ID: {currentCam}")
        currentCam -= 1
        continue

    outputStream.putFrame(frame)

cap.release()
