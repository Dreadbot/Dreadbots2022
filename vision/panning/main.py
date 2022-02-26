import cv2
import numpy

camera = cv2.VideoCapture(0)

panning_width = 720
panning_lower = 280

cv2.namedWindow("Camera")

def on_change(value):
    global panning_lower

    panning_lower = value
    cv2.setTrackbarPos("Panning", "Camera", value)

cv2.createTrackbar("Panning", "Camera", panning_lower, 1280, on_change)

while True:
    _, frame = camera.read()

    cv2.imshow("Raw", frame)
    cv2.imshow("Camera", frame[:,panning_lower:(panning_lower + panning_width),:])

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
