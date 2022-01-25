from sys import getsizeof
import cv2
import numpy as np
import math
import json

### TO DO ###
#- Convert filtering to a pipeline
#- Convert contour stuff to pipeline
#- Make the output 
#- Probably more stuff idk right now

# Create functions to handle profiles
profiles = {}
values = {}
def createBlankProfile(name):
    profiles[name] = {
        'High H' : 70,
        'Low H' : 60,
        'High S' : 60,
        'Low S' : 40,
        'High L' : 255,
        'Low L' : 200,
        'Upper Height' : 30,
        'Lower Height' : 20,
        'Upper Width' : 30,
        'Lower Width' : 3
    }
def loadProfile(profile):
    cv2.setTrackbarPos('High H', 'Trackbars', profiles[profile]['High H'])
    cv2.setTrackbarPos('Low H', 'Trackbars', profiles[profile]['Low H'])
    cv2.setTrackbarPos('High S', 'Trackbars', profiles[profile]['High S'])
    cv2.setTrackbarPos('Low S', 'Trackbars', profiles[profile]['Low S'])
    cv2.setTrackbarPos('High L', 'Trackbars', profiles[profile]['High L'])
    cv2.setTrackbarPos('Low L', 'Trackbars', profiles[profile]['Low L'])
    cv2.setTrackbarPos('Upper Height', 'Trackbars', profiles[profile]['Upper Height'])
    cv2.setTrackbarPos('Lower Height', 'Trackbars', profiles[profile]['Lower Height'])
    cv2.setTrackbarPos('Upper Width', 'Trackbars', profiles[profile]['Upper Width'])
    cv2.setTrackbarPos('Lower Width', 'Trackbars', profiles[profile]['Lower Width'])
def saveProfile(profile):
    profiles[profile]['High H'] = cv2.getTrackbarPos('High H', 'Trackbars')
    profiles[profile]['Low H'] = cv2.getTrackbarPos('Low H', 'Trackbars')
    profiles[profile]['High S'] = cv2.getTrackbarPos('High S', 'Trackbars')
    profiles[profile]['Low S'] = cv2.getTrackbarPos('Low S', 'Trackbars')
    profiles[profile]['High L'] = cv2.getTrackbarPos('High L', 'Trackbars')
    profiles[profile]['Low L'] = cv2.getTrackbarPos('Low L', 'Trackbars')
    profiles[profile]['Upper Height'] = cv2.getTrackbarPos('Upper Height', 'Trackbars')
    profiles[profile]['Lower Height'] = cv2.getTrackbarPos('Lower Height', 'Trackbars')
    profiles[profile]['Upper Width'] = cv2.getTrackbarPos('Upper Width', 'Trackbars')
    profiles[profile]['Lower Width'] = cv2.getTrackbarPos('Lower Width', 'Trackbars')

def _drawTargets(x, y, w, h, rectangleColor, circleColor, lineColor) : 
    cv2.rectangle(imgToPush, (x,y), (x+w, y+h), rectangleColor, 3)
    cv2.circle(imgToPush, (int(x+(w/2)), int(y+(h/2))), 5, (255, 0, 0))
    
    target = [int(x+(w/2)), int(y+(h/2))]

    cv2.line(imgToPush, (target[0]-10, target[1]), (target[0]+10, target[1]), (255,255,255))
    cv2.line(imgToPush, (target[0], target[1]-10), (target[0], target[1]+10), (255,255,255))
    return target

# Create a VideoCaqpture object and read from input file
cap = cv2.VideoCapture(1)
# Create varibles for trackbars
file = open('targetingCal.json')
profiles = json.load(file)
file.close()
if len(profiles) == 0:
    createBlankProfile('Default')
high_H, high_S, high_L, low_H, low_S, low_L = 0, 0, 0, 0, 0, 0
upper_hight, lower_height, upper_width, lower_width = 0, 0, 0, 0
HIGH_VAL_HSL = 255
HIGH_H = 360 // 2
HIGH_VAL_BOUNDING_BOX = 50
# Set the exposure of the camera to help with finding the target
cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
cap.set(cv2.CAP_PROP_EXPOSURE, -15)
#create function for trackbars
def ontrackbar(val):
    pass
# Add window for trackbars and create trackbars
cv2.namedWindow('Trackbars')
cv2.resizeWindow('Trackbars', 500, 450)
cv2.createTrackbar('High H', 'Trackbars', high_H, HIGH_H, ontrackbar)
cv2.createTrackbar('Low H', 'Trackbars', low_H, HIGH_H, ontrackbar)
cv2.createTrackbar('High S', 'Trackbars', high_S, HIGH_VAL_HSL, ontrackbar)
cv2.createTrackbar('Low S', 'Trackbars', low_S, HIGH_VAL_HSL, ontrackbar)
cv2.createTrackbar('High L', 'Trackbars', high_L, HIGH_VAL_HSL, ontrackbar)
cv2.createTrackbar('Low L', 'Trackbars', low_L, HIGH_VAL_HSL, ontrackbar)
cv2.createTrackbar('Upper Height', 'Trackbars', upper_hight, HIGH_VAL_BOUNDING_BOX, ontrackbar)
cv2.createTrackbar('Lower Height', 'Trackbars', lower_height, HIGH_VAL_BOUNDING_BOX, ontrackbar)
cv2.createTrackbar('Upper Width', 'Trackbars', upper_width, HIGH_VAL_BOUNDING_BOX, ontrackbar)
cv2.createTrackbar('Lower Width', 'Trackbars', lower_width, HIGH_VAL_BOUNDING_BOX, ontrackbar)
profile = input('Profile?:\n')
if profile in profiles:
    loadProfile(profile)
else:
    createBlankProfile(profile)
    loadProfile(profile)
# Check if camera opened successfully
if (cap.isOpened()== False): 
    print("Error opening video  file")

# Read until video is completed
while(cap.isOpened()):
      
# Capture frame-by-frame
  ret, inputImg = cap.read()
  if ret == True:
# applies many filters those filters beiiing...
    hlsImg = cv2.cvtColor(inputImg, cv2.COLOR_BGR2HLS) # Converting the input to HLS color
    # add hsl values
    h = [cv2.getTrackbarPos('Low H', 'Trackbars'), cv2.getTrackbarPos('High H', 'Trackbars')]
    s = [cv2.getTrackbarPos('Low S', 'Trackbars'), cv2.getTrackbarPos('High S', 'Trackbars')]   #- find the HSL values for different environments/lights in the README.md
    l = [cv2.getTrackbarPos('Low L', 'Trackbars'), cv2.getTrackbarPos('High L', 'Trackbars')]
    blurImg =  cv2.GaussianBlur(hlsImg, (7, 7), 0) # Blurs the HLS image a bit to make it easier to work with
    maskImg = cv2.inRange(blurImg, (h[0], s[0], l[0]), (h[1], s[1], l[1])) # Checks all pixels and changes the ones outside the range to black and the ones in to white
    dilateImg = cv2.dilate(maskImg, (7, 7), 20) # Dilates out all the found sections so we can get them more solid

# Finds then adds the contours over the original image
    contours, hierarchy = cv2.findContours(image=dilateImg, mode=cv2.RETR_TREE, method=cv2.CHAIN_APPROX_NONE)
    contourImg = cv2.drawContours(image=inputImg, contours=contours, contourIdx=-1, color=(0, 0, 255), thickness=2, lineType=cv2.LINE_AA)



# For testing, shows the video feed in different states in windows on your computer
    #cv2.imshow('Input', inputImg)
    # cv2.imshow('HLS Conversion', hlsImg)
    # cv2.imshow('Mask', maskImg)
    cv2.imshow('Dilated', dilateImg)
    cv2.imshow('Contour', contourImg)

    imgToPush = cv2.add(inputImg, np.array([50.]))

# Loops through and adds the targets over the original image
    for c in contours : 

# Creates and then seperates the bounds of positioning and width and height of the camera input
      bounds = cv2.boundingRect(c) 
      x, y, w, h = bounds

# Prep stuff, do not touch until we are calibrating to this years bot, right now its based on 2020 bot
      totPixels = w * h
      filledPixels = 0
      checkedPixels = 0
      img_h = inputImg.shape[0]
      img_w = inputImg.shape[1]
      target_found = False
      flength = 544
      camOffset = 22
      targetHeight = 72


# Checks the width (w) and height (h) of every contour in the frame and only puts the targets over the ones in the range
      if w > cv2.getTrackbarPos('Lower Width', 'Trackbars') and w < cv2.getTrackbarPos('Upper Width', 'Trackbars') and h > cv2.getTrackbarPos('Lower Height', 'Trackbars') and h < cv2.getTrackbarPos('Upper Height', 'Trackbars') :   # w 5,20   h 25,40
#Draws the target over the reflective tape on the original image
        target = _drawTargets(x, y, w, h, (0,255,0), (255,0,0), (255,255,255))
# Calculate angle to turn to
        fin_angle_hori = ((math.atan((target[0]-(img_w/2))/flength)))*(180/math.pi)
        
#Calculate vertical angle for distance calculations (LOTS of fancy math cole did)
        fin_angle_raw_rad = math.atan((240 - target[1])/flength)
        fin_angle_deg = math.degrees(fin_angle_raw_rad) + camOffset
        fin_angle_rad = math.radians(fin_angle_deg)
        distance = targetHeight / math.tan(fin_angle_rad)

        fin_angle_hori = ((math.atan((target[0]-(img_w/2))/flength)))*(180/math.pi)

        fin_angle_raw_rad = math.atan((240 - target[1])/flength)
        fin_angle_deg = math.degrees(fin_angle_raw_rad) + camOffset
        fin_angle_rad = math.radians(fin_angle_deg)
        distance = targetHeight / math.tan(fin_angle_rad)
        #print('yesadfdfgdfg')
        # scale image
        width = int(imgToPush.shape[1] * 160 / 100)
        height = int(imgToPush.shape[0] * 160 / 100)
        dim = (width, height)
        resized = cv2.resize(imgToPush, dim)
        cv2.imshow('pushed image', resized) # Show final image on your computer with targets shown

# Press Q on keyboard to  exit
    if cv2.waitKey(1) & 0xFF == ord('q'):
      saveProfile(profile)
      break
   
# Break the loop
  else: 
    break
# Save to json
with open('targetingCal.json', 'w') as file:
    json.dump(profiles, file)
# When everything done, release the video capture object and close any open windows that show video feeds
cap.release()
cv2.destroyAllWindows()