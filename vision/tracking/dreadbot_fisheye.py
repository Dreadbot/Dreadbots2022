import cv2
import numpy as np
import json

class Fisheye:
    def __init__(self, cam_id):
        self.cam_id = str(cam_id)
        
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
