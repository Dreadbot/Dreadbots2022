import cv2
import numpy as np
import math


### TO DO ###
#- Convert filtering to a pipeline
#- Convert contour stuff to pipeline
#- IMPORTANT Calibrate the Height of the hw threshold to the actual target height (width already done)
#- Probably more stuff idk right now


# Create a VideoCaqpture object and read from input file
cap = cv2.VideoCapture(1)

# Set the exposure of the camera to help with finding the target
cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
cap.set(cv2.CAP_PROP_EXPOSURE, -15)

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
    h = [60, 70]       #| 60,70
    s = [40, 60]      #| 40,60    --- these three values are the range that we use to convert to binary image
    l = [200, 255]     #| 200,255
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
      if w > 5 and w < 20 and h > 25 and h < 40 :   # w 5,15   h 10,150
#Draws the target over the reflective tape on the original image
        cv2.rectangle(imgToPush, (x,y), (x+w, y+h), (0, 255, 0), 3)
        cv2.circle(imgToPush, (int(x+(w/2)), int(y+(h/2))), 5, (255, 0, 0))
        
        target = [int(x+(w/2)), int(y+(h/2))]

        cv2.line(imgToPush, (target[0]-10, target[1]), (target[0]+10, target[1]), (255,255,255))
        cv2.line(imgToPush, (target[0], target[1]-10), (target[0], target[1]+10), (255,255,255))

# Calculate angle to turn to
        fin_angle_hori = ((math.atan((target[0]-(img_w/2))/flength)))*(180/math.pi)
        
#Calculate vertical angle for distance calculations (LOTS of fancy math cole did)
        fin_angle_raw_rad = math.atan((240 - target[1])/flength)
        fin_angle_deg = math.degrees(fin_angle_raw_rad) + camOffset
        fin_angle_rad = math.radians(fin_angle_deg)
        distance = targetHeight / math.tan(fin_angle_rad)
        target_found = True

        fin_angle_hori = ((math.atan((target[0]-(img_w/2))/flength)))*(180/math.pi)

        fin_angle_raw_rad = math.atan((240 - target[1])/flength)
        fin_angle_deg = math.degrees(fin_angle_raw_rad) + camOffset
        fin_angle_rad = math.radians(fin_angle_deg)
        distance = targetHeight / math.tan(fin_angle_rad)
        
        target_found = True

        #print('yesadfdfgdfg')
        cv2.imshow('pushed image', imgToPush) # Show final image on your computer with targets shown

# Press Q on keyboard to  exit
    if cv2.waitKey(1) & 0xFF == ord('q'):
      break
   
# Break the loop
  else: 
    break
   
# When everything done, release the video capture object and close any open windows that show video feeds
cap.release()
cv2.destroyAllWindows()