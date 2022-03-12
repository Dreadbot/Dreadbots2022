import cv2

class Panner:
    def __init__(self, width=100):
        self.position = 0
        self.width = width

    def get_panned_image(self, img):
        return img[:,min(int(self.position),int(img.shape[1]-self.width)):min(int(self.position+self.width),img.shape[1]),:]

