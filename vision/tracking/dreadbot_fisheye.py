import cv2
import numpy as np
import json

#Blend two images together with linear transparency gradient
def image_blend(src1, src2, overlap_width):
    assert src1.shape == src2.shape, "Blend inputs must match shape"

    # Define image dimensions
    base_w = 640
    base_h = 480

    # Set the width of the sum image
    sum_img_w = 2*base_w - overlap_width

    # Create blank sum image
    sum_img = np.zeros([base_h, sum_img_w, 3], dtype=np.uint8)

    # Calculate the slope of the linear transparency gradient given the overlap_width (total strips)
    transparency_step = 1/overlap_width

    # Where the non-overlap src1 input stops
    src1_stop = base_w-overlap_width

    # Insert the non-overlap portions of src1 & src2 into the sum image
    sum_img[0:base_h, 0:src1_stop] = src1[0:base_h, 0:src1_stop]
    sum_img[0:base_h, base_w:sum_img_w] = src2[0:base_h, overlap_width:base_w]

    # Create new image of just the overlap
    overlapped_region = sum_img[0:base_h, src1_stop:base_w]

    # Initialize the transparency weights
    src1_weight = 1
    src2_weight = 0

    # Create new images of each src image's overlapped portions
    src1_overlap = src1[0:base_h, base_w-overlap_width:base_w]
    src2_overlap = src2[0:base_h, 0:overlap_width]

    # Loop through each column in overlapped region
    for col in range(overlap_width):
        # Grab each overlapped src image's current column
        src1_strip = src1_overlap[0:base_h, col:col+1]
        src2_strip = src2_overlap[0:base_h, col:col+1]

        # If for some reason either src image's current column doesn't exist create a blank strip
        if not src1_strip.any():
            src1_strip = np.zeros([base_h, 1, 3], dtype=np.uint8)
        
        if not src2_strip.any():
            src2_strip = np.zeros([base_h, 1, 3], dtype=np.uint8)

        # cv2.addWeighted outputs the overlapped strip with transparency
        # Copy the new overlapped strip over to the full overlap portion
        overlapped_region[0:base_h, col:col+1] = cv2.addWeighted(src1_strip, src1_weight, src2_strip, src2_weight, 0)

        # Adjust the transparency weights with respect to the gradient slope
        src1_weight -= transparency_step
        src2_weight += transparency_step

    # Copy the now overlapped section over to the sum image
    sum_img[0:base_h, src1_stop:base_w] = overlapped_region

    # Output the sum image
    return(sum_img)


class Fisheye:
    def __init__(self, capture_id, fisheye_id, adj_exposure=None):
        # JSON Recognizes the camera IDs as strings, so convert the integer input into a string
        self.cam_id = str(fisheye_id)
        
        # Load the fisheye calibration file in as confs_loaded
        with open('calibrations.json', 'r') as f:
            confs_loaded = json.load(f)
        
        # Define a bunch of camera attributes according to the loaded configuration
        self.K = np.array(confs_loaded[self.cam_id]['K']) # Camera Intrinsic Matrix
        self.D = np.array(confs_loaded[self.cam_id]['D']) # Camera Distortion Matrix

        self.fx = self.K[0,0] # Focal X
        self.fy = self.K[1,1] # Focal Y

        self.x = self.K[0,2] # X Offset
        self.y = self.K[1,2] # Y Offset

        self.axis_skew = self.K[0,1] # Axis Skew

        self.DIM = (confs_loaded[self.cam_id]['DIM'][0], confs_loaded[self.cam_id]['DIM'][1]) # Output dimensions

        # Calculate the undistortion maps
        self.map1, self.map2 = cv2.fisheye.initUndistortRectifyMap(self.K, self.D, np.eye(3), self.K, self.DIM, cv2.CV_16SC2)

        # Create OpenCV capture object
        self.cap = cv2.VideoCapture(capture_id)

        # If the user defined an exposure set it to that, otherwise keep default and disable auto exposure
        if adj_exposure is not None:
            self.cap.set(cv2.CAP_PROP_EXPOSURE, adj_exposure)
        else:
            self.cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)

    # Returns raw image from camera (with fisheye distortion)
    def retrieve_distorted_img(self):
        ret, img = self.cap.read()
        return img

    # Returns undistorted image from camera using undistort maps defined earlier
    def retrieve_undistorted_img(self):
        img = self.retrieve_distorted_img()
        return cv2.remap(img, self.map1, self.map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

    # Converts undistorted image coordinates back to distorted coordinates
    def reverse_project_point(self, x, y):
        rotation_vector = np.array([[[0., 0., 0.]]])
        translation_vector = np.array([[[0., 0., 0.]]])
        
        obj_point = np.array([[[(x-self.x)/self.fx, (y-self.y)/self.fy, 0]]])

        projected_points, _ = cv2.fisheye.projectPoints(obj_point, rotation_vector, translation_vector, self.K, self.D)
        unwrapped_points = (int(projected_points[0, 0, 0]), int(projected_points[0, 0, 1]))

        return(unwrapped_points)

    # Returns horizontal and vertical angle with respect to the optical axis
    def calculate_angle(self, x, y):
        # Convert undistorted image coordinates back to raw image coordinates
        found_x, found_y = self.reverse_project_point(x, y)

        # Define center variables
        center_x, center_y = (320,240)

        # Define and undistort the horizontal calibrated point for the horizontal axis
        horizontal_calibrated_point = self.reverse_project_point(87,255)
        horizontal_calibrated_x, _ = horizontal_calibrated_point # Second value (Y coordinate) ignored as it is not used

        horizontal_calibrated_angle = 47.897 # Degrees

        # Define and undistort the vertical calibrated point for the vertical axis
        vertical_calibrated_point = self.reverse_project_point(330, 18)
        _, vertical_calibrated_y = vertical_calibrated_point # First value (X coordinate) ignored as it is not used

        vertical_calibrated_angle = 41.139 # Degrees

        # Calculate the calibrated points' distances from the center of the image
        calibrated_dx = abs(horizontal_calibrated_x-center_x)
        calibrated_dy = abs(vertical_calibrated_y-center_y)

        # Calculate the input points' distances from the center of the image
        found_dx = abs(found_x-center_x)
        found_dy = abs(found_y-center_y)

        # Calculate the horizontal and vertical angle to the given point with respect to the optical axis
        horizontal_angle = (horizontal_calibrated_angle/calibrated_dx) * found_dx
        vertical_angle = (vertical_calibrated_angle/calibrated_dy) * found_dy

        '''
        The relationship used above is defined as angle_1 / pixel_distance_1   =   angle_2 / pixel_distance_2
        The defined calibrated points are points where angle and pixel distances are knowns, and we can find the pixel distances to
        the inputted point, so we rearrange the relationship to calculate angle_1 such that
        angle_1 = (angle_2 / pixel_distance_2) * pixel_distance_1
        '''

        
        # Output calculated angles
        return horizontal_angle, vertical_angle

    # Unloads the camera's OpenCV capture object
    def unload(self):
        self.cap.release()