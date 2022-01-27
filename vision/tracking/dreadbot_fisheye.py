import cv2
import numpy as np
import json


def detect(img, draw=False):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    out_img = img.copy()
    descriptor = cv2.SIFT_create()

    key_pts, features = descriptor.detectAndCompute(img, None)

    if draw:
        cv2.drawKeypoints(img, key_pts, out_img)
        out_img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
        return (key_pts, features, out_img.copy())

    else:
        return (key_pts, features)


class Fisheye:
    def __init__(self, capture_id, fisheye_id):
        self.cam_id = str(fisheye_id)
        
        with open('calibrations.json', 'r') as f:
            confs_loaded = json.load(f)
        
        self.K = np.array(confs_loaded[self.cam_id]['K'])
        self.D = np.array(confs_loaded[self.cam_id]['D'])

        self.fx = self.K[0,0]
        self.fy = self.K[1,1]

        self.x = self.K[0,2]
        self.y = self.K[1,2]

        self.axis_screw = self.K[0,1]

        self.DIM = (confs_loaded[self.cam_id]['DIM'][0], confs_loaded[self.cam_id]['DIM'][1])

        self.map1, self.map2 = cv2.fisheye.initUndistortRectifyMap(self.K, self.D, np.eye(3), self.K, self.DIM, cv2.CV_16SC2)

        self.cap = cv2.VideoCapture(capture_id)
        self.cap.set(cv2.CAP_PROP_EXPOSURE, 70)

    def ret_raw(self):
        ret, img = self.cap.read()
        return img

    def ret_undist(self):
        img = self.ret_raw()
        return cv2.remap(img, self.map1, self.map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)
        

    def unload(self):
        self.cap.release()