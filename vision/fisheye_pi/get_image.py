import cv2
import dreadbot_fisheye as df
import os

cap =  df.Fisheye(0, 0, -4)

img = 0

while True:
    ret, frame = cap.retrieve_undistorted_img()

    if not ret: break
    
    cv2.imshow("Frame", frame)

    key = cv2.waitKey(1) & 0xFF

    if key == ord("p"):
        cv2.imwrite(os.path.join(os.path.dirname(os.path.realpath(__file__)), "testing", f"image{img}.png"), frame)
        img += 1
    elif key == ord("q"):
        break


cap.unload()
