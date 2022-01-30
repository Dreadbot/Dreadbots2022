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


def image_blend(src1, src2, overlap_width):
    assert src1.shape == src2.shape, "Blend inputs must match shape"

    # base_w, base_h, _ = src1.shape
    base_w = 640
    base_h = 480

    sum_img_w = 2*base_w - overlap_width
    sum_img = np.zeros([base_h, sum_img_w, 3], dtype=np.uint8)

    transparency_step = 1/overlap_width

    src1_stop = base_w-overlap_width

    sum_img[0:base_h, 0:src1_stop] = src1[0:base_h, 0:src1_stop]
    sum_img[0:base_h, base_w:sum_img_w] = src2[0:base_h, overlap_width:base_w]

    overlapped_region = sum_img[0:base_h, src1_stop:base_w]

    src1_weight = 1
    src2_weight = 0

    src1_overlap = src1[0:base_h, base_w-overlap_width:base_w]
    src2_overlap = src2[0:base_h, 0:overlap_width]

    for col in range(overlap_width):
        src1_strip = src1_overlap[0:base_h, col:col+1]
        src2_strip = src2_overlap[0:base_h, col:col+1]

        if not src1_strip.any():
            src1_strip = np.zeros([base_h, 1, 3], dtype=np.uint8)
        
        if not src2_strip.any():
            src2_strip = np.zeros([base_h, 1, 3], dtype=np.uint8)

        overlapped_region[0:base_h, col:col+1] = cv2.addWeighted(src1_strip, src1_weight, src2_strip, src2_weight, 0)

        src1_weight -= transparency_step
        src2_weight += transparency_step

    
    sum_img[0:base_h, src1_stop:base_w] = overlapped_region

    return(sum_img)


class Fisheye:
    def __init__(self, capture_id, fisheye_id, adj_exposure=None):
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

        if not adj_exposure:
            self.cap.set(cv2.CAP_PROP_EXPOSURE, adj_exposure)
        else:
            self.cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.75)

    def ret_dist(self):
        ret, img = self.cap.read()
        return img

    def ret_undist(self):
        img = self.ret_dist()
        return cv2.remap(img, self.map1, self.map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    def reverse_project_point(self, x, y):
        rvec = np.array([[[0., 0., 0.]]])
        tvec = np.array([[[0., 0., 0.]]])
        
        obj_point = np.array([[[(x-self.x)/self.fx, (y-self.y)/self.fy, 0]]])

        rev_pts, rev_jac = cv2.fisheye.projectPoints(obj_point, rvec, tvec, self.K, self.D)
        rev_euc = (int(rev_pts[0, 0, 0]), int(rev_pts[0, 0, 1]))

        return(rev_euc)

    def unload(self):
        self.cap.release()