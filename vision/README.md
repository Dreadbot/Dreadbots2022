# 2022 Dreadbot Vision

Code used for vision targeting in the 2022 FIRST Robotics Competition: Rapid React

## Contents

[Work Done This Season](#work-done-this-season)

[Organization](#organization)

## Work Done This Season

### Fisheye

Wide angle lenses were wanted for both driver cameras, and wide FOV vision processing, so a large chunk of the build season was spent learning how to work 
with fisheyes. The immense radial distortion presented the first, and the largest, roadblock. Within the center of a fisheye lens the image comes out
clearly. As a point moves to the edge of the image sensor, it gets exponentially distorted, and that distortion makes image processing borderline
impossible. Once we worked to undistort the images we were getting, the next problem involved image-stitching for drivers. Generic panoramic stitching 
involving perspective warping from feature mapping + homography mapping proved too expensive resourcefully, so a cheap method was developed to put two 
images together by linearly gradienting their transparencies in/out of each other in their overlap zones. The final problem was to discern a method to 
derive real-world information from the fisheye lenses, considering normal pinhole projection wouldn't work. The relationship shown in Fig. 2b in
https://www.isprs.org/proceedings/xxxvi/5-W8/Paper/PanoWS_Berlin2005_Schwalbe.pdf was tested and held true. While the two elementary angles given, yaw and 
pitch, aren't enough to form full real-world vectors, the information given by them was enough to move forward.

From there all of that work was put under one wrapper, ```dreadbot_fisheye.py``` within ```fisheye_pi```, which can be used going forward
(Seperate documentation WIP, code fully commented)

### Hub-Tracking


### Cargo-Tracking


### PI Infrastructure


## Organization

```turret_pi``` - Files related to hub tracking for turret control

```fisheye_pi``` - Files related to fisheye handling for driver monitor and game piece tracking


