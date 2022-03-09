import numpy as np
import math
import cv2
import os

class space:
    def __init__(self, w, h):
        self.w = w
        self.h = h

        self.origin_x = w//2
        self.origin_y = (h//2) + (h//4)

        self.origin = (self.origin_x, self.origin_y)

        self.img = np.zeros([w,h,3], dtype=np.uint8)
        self.img[::] = [255,255,255]

        cv2.line(self.img, (0, self.origin_y), (self.w, self.origin_y), (0,0,0)) #x-axis
        cv2.line(self.img, (self.origin_x, 0), (self.origin_x, self.h), (0,0,0)) #y-axis

    def world_img(self, pt):
        u, v = pt
        adj_u, adj_v = (u+self.origin_x, self.origin_y-v)

        adj_u, adj_v = int(adj_u), int(adj_v)
        return((adj_u, adj_v))

    def circle(self, pt, color, radius=2, thickness=-1):
        pt = self.world_img(pt)

        if pt[1] < 0:
            return

        cv2.circle(self.img, pt, radius, color, thickness=thickness)

    def line(self, pt1, pt2, color):
        pt1 = self.world_img(pt1)
        pt2 = self.world_img(pt2)

        cv2.line(self.img, pt1, pt2, color)

    def reset_img(self):
        self.img[::] = [255,255,255]

        cv2.line(self.img, (0, self.origin_y), (self.w, self.origin_y), (0,0,0)) #x-axis
        cv2.line(self.img, (self.origin_x, 0), (self.origin_x, self.h), (0,0,0)) #y-axis

    def retrieve_img(self):
        out = self.img.copy()
        self.reset_img()
        return(out)

    