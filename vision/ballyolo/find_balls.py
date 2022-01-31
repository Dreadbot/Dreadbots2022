# -- DEPRECATED --
import cv2
import torch
import pandas as pd

# Model
model = torch.hub.load('ultralytics/yolov5', 'yolov5n', device='cpu', classes=32)

cap = cv2.VideoCapture(0)

while True:
    _, frame = cap.read()
    results = model(frame)
    df = results.pandas().xyxy[0]

    for _,row in df.iterrows():
        cv2.rectangle(frame, (int(row['xmin']), int(row['ymin'])), (int(row['xmax']), int(row['ymax'])), (0,255,0), 3)

    cv2.imshow('frame', frame)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
