from email.mime import base
import cv2
import numpy as np
import os
import json
import shutil

cap = cv2.VideoCapture(3)

# Folder convention - images in f_calib_<n>

fisheye_id = input("FISHEYE Camera ID: ")

base_path = 'f_calib_{0}'.format(fisheye_id)
os.mkdir(base_path)

image_count = 0

imposed_image = None

def snap_and_save(image):
    global fisheye_id
    global image_count

    path = base_path+"/{0}.png".format(image_count)
    image_count += 1

    cv2.imwrite(path, image)
    print("Images: {0}".format(image_count))
    return image

def calibrate():
    global fisheye_id
    global base_path

    CHECKERBOARD = (7,9)

    subpix_criteria = (cv2.TERM_CRITERIA_EPS+cv2.TERM_CRITERIA_MAX_ITER, 30, 0.1)

    calibration_flags = cv2.fisheye.CALIB_RECOMPUTE_EXTRINSIC+cv2.fisheye.CALIB_FIX_SKEW

    objp = np.zeros((1, CHECKERBOARD[0]*CHECKERBOARD[1], 3), np.float32)
    objp[0,:,:2] = np.mgrid[0:CHECKERBOARD[0], 0:CHECKERBOARD[1]].T.reshape(-1, 2)

    _img_shape = None

    objpoints = []
    imgpoints = []

    images = os.listdir(base_path)


    for i in range(0, len(images)):
        images[i] = base_path+"/{0}".format(images[i])

    print(images)

    for file in images:
        img = cv2.imread(file)
        if _img_shape == None:
            _img_shape = img.shape[:2]
        else:
            assert _img_shape == img.shape[:2], "All images must share the same size."

        gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        ret, corners = cv2.findChessboardCorners(gray_img, CHECKERBOARD, cv2.CALIB_CB_ADAPTIVE_THRESH+cv2.CALIB_CB_FAST_CHECK+cv2.CALIB_CB_NORMALIZE_IMAGE)

        if ret == True:
            objpoints.append(objp)
            cv2.cornerSubPix(gray_img,corners,(3,3),(-1,-1),subpix_criteria)
            imgpoints.append(corners)

    N_OK = len(objpoints)

    K = np.zeros((3, 3))
    D = np.zeros((4, 1))

    rvecs = [np.zeros((1, 1, 3), dtype=np.float64) for i in range(N_OK)]
    tvecs = [np.zeros((1, 1, 3), dtype=np.float64) for i in range(N_OK)]

    rms, _, _, _, _ = \
        cv2.fisheye.calibrate(
            objpoints,
            imgpoints,
            gray_img.shape[::-1],
            K,
            D,
            rvecs,
            tvecs,
            calibration_flags,
            (cv2.TERM_CRITERIA_EPS+cv2.TERM_CRITERIA_MAX_ITER, 30, 1e-6)
        )

    print("Found " + str(N_OK) + " valid images for calibration")
    print("DIM=" + str(_img_shape[::-1]))
    print("K=np.array(" + str(K.tolist()) + ")")
    print("D=np.array(" + str(D.tolist()) + ")")
    print("\n")

    verdict = input("Are the above parameters acceptable for writing? [y/n]")

    if verdict == 'y' or verdict == 'Y':
        with open('calibrations.json', 'r') as f:
            base_config = json.load(f)

        new_entry = {
                "DIM" : _img_shape[::-1],
                "K" : K.tolist(),
                "D" : D.tolist()
        }

        base_config[str(fisheye_id)] = new_entry
        with open('calibrations.json', 'w') as f:
            json.dump(base_config, f)

        print("Calibrations saved! Exiting.")

print('''
Keys:
p - Take and save image
c - Calibrate w/ taken images & exit
q - Exit w/o calibrating
''')


while True:
    ret, img = cap.read()
    raw_img = img.copy()

    h, w, _ = img.shape
    cx = w//2
    cy = h//2

    cv2.circle(img, (cx, cy), 5, (0,0,255), thickness=-1)

    cv2.line(img, (0,0), (w,h), (0,0,255))
    cv2.line(img, (0,h), (w,0), (0,0,255))
    cv2.line(img, (0,cy), (w,cy), (0,0,255))
    cv2.line(img, (cx,0), (cx,h), (0,0,255))

    if imposed_image is not None:
        img = cv2.addWeighted(img, 0.5, imposed_image, 0.5, 1)
    


    cv2.imshow('frame', img)

    key = cv2.waitKey(1) & 0xFF

    if key == ord('q'):
        break
    elif key == ord('p'):
        imposed_image = snap_and_save(raw_img)
    elif key == ord('c'):
        calibrate()
        break

shutil.rmtree(base_path)
cv2.destroyAllWindows()
cap.release()