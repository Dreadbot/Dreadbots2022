from cscore import CameraServer
import numpy as np
import argparse
import curses
import json
import math
import util
import cv2
import os

argparser = argparse.ArgumentParser(description="Runs a slider program to determine masks")
argparser.add_argument('-n', '--number', default=0, help="Image number", type=int, dest='imgnum')
argparser.add_argument('-m', '--mask-output', default='mask.png', help="Destination file for the mask", dest='maskf')
args = argparser.parse_args()

# Get the image
img = cv2.imread(os.path.join("testing", "image%d.png" % args.imgnum))

# Open the mask_json.json file
mask_json = open("mask_json.json", 'r+')

def main(stdscr):
    # Read initial values from the json file
    mask_json.seek(0)
    init_data = json.load(mask_json)
    if init_data.get('lower') is None:
        init_data['lower'] = {'h': 0, 's': 0, 'v': 0}
    if init_data.get('upper') is None:
        init_data['upper'] = {'h': 255, 's': 255, 'v': 255}

    # Hide the cursor
    curses.curs_set(0)
    # Start colour mode in the terminal
    curses.start_color()
    
    # Initialise slider arrays`
    sliders = [[init_data['lower']['h'], init_data['upper']['h'], 0, 255, "H"], [init_data['lower']['s'], init_data['upper']['s'], 0, 255, "S"], [init_data['lower']['v'], init_data['upper']['v'], 0, 255, "V"]]
    # Initialise status variables
    cur_slider = 0
    left = True
    
    # Constant that is the length (in chars) of the slider bar
    slider_length = 50

    while True:
        # Clear the screen every frame
        stdscr.clear()

        # Number of sliders
        length = len(sliders)
        
        # Set the lower and upper bounds of the mask
        lb = np.array([sliders[0][0], sliders[1][0], sliders[2][0]])
        ub = np.array([sliders[0][1], sliders[1][1], sliders[2][1]])

        mask = util.getMask(img, lb, ub, 4, 4, 1)
        img_overlay = cv2.bitwise_and(img, img, mask=mask)
        cv2.imwrite(args.maskf, img_overlay)

        # Draw all of the sliders
        for i in range(0, length):
            # Slider range
            slider_range = sliders[i][3] - sliders[i][2]

            # Number of characters on each part of the slider
            num_left = math.floor((sliders[i][0]/slider_range) * slider_length)
            num_right = math.floor((1 - (sliders[i][1]/slider_range)) * slider_length)
            num_unfilled = slider_length - num_left - num_right

            # Get the color pair in the gradient for this specific slider
            color_pair = curses.color_pair((i % 3) + 10)

            # Draw all of the parts of the sliders
            stdscr.addstr(i, 0, '%3d ' % sliders[i][0], color_pair)
            stdscr.addstr(i, 4, '%s' % '>'*num_left, color_pair)
            stdscr.addstr(i, 4 + num_left, '%s' % '-'*num_unfilled)
            stdscr.addstr(i, 4 + num_left + num_unfilled, '%s' % '<'*num_right, color_pair)
            stdscr.addstr(i, 4 + num_left + num_unfilled + num_right, ' %3d' % sliders[i][1], color_pair)
            stdscr.addstr(i, 4 + num_left + num_unfilled + num_right + 4, ' %s ' % sliders[i][4], curses.A_ITALIC)

            # If this is the currently selected slider, draw the pointer
            if i == cur_slider:
                stdscr.addstr(i, 4 + num_left + num_unfilled + num_right + 4 + len(sliders[i][4]) + 2, "<--", curses.color_pair(2) | curses.A_BOLD)

        # Draw the directional indicator based on which side of the slider you are editing
        if left:
            stdscr.addstr(length+1, 1, '<-- Direction', curses.color_pair(1) | curses.A_BOLD)
        else:
            stdscr.addstr(length+1, 1, '--> Direction', curses.color_pair(1) | curses.A_BOLD)

        # Refresh the screen, with all of the new strings drawn on it
        stdscr.refresh()

        # Wait for a keypress
        key = stdscr.getch(cur_slider, slider_length + 8)
        if key == ord('q'):
            break # Exit the program loop
        elif key == ord('s'):
            left = not left # Switch directions
        elif key == curses.KEY_RIGHT and left:
            # Increase the left slider up to the position of the other slider
            sliders[cur_slider][0] = min(min(sliders[cur_slider][0] + 1, sliders[cur_slider][3]), sliders[cur_slider][1])
        elif key == curses.KEY_RIGHT and not left:
            # Increase the right slider up to the max of the slider
            sliders[cur_slider][1] = max(min(sliders[cur_slider][1] + 1, sliders[cur_slider][3]), sliders[cur_slider][0])
        elif key == curses.KEY_LEFT and left:
            # Decrease the left slider down to the min of the slider
            sliders[cur_slider][0] = min(max(sliders[cur_slider][0] - 1, sliders[cur_slider][2]), sliders[cur_slider][1])
        elif key == curses.KEY_LEFT and not left:
            # Decrease the right slider down to the position of the other slider
            sliders[cur_slider][1] = max(max(sliders[cur_slider][1] - 1, sliders[cur_slider][2]), sliders[cur_slider][0])
        elif key == curses.KEY_DOWN:
            # Select the next slider down, but don't go over
            cur_slider = min(cur_slider + 1, len(sliders)-1)
            continue
        elif key == curses.KEY_UP:
            # Select the next slider up, but don't go over
            cur_slider = max(cur_slider - 1, 0)
            continue
        else:
            continue
        
        # Write to the json file
        mask_json.truncate(0)
        mask_json.seek(0)
        json.dump({
            "lower": {
                "h": sliders[0][0],
                "s": sliders[1][0],
                "v": sliders[2][0],
            },
            "upper": {
                "h": sliders[0][1],
                "s": sliders[1][1],
                "v": sliders[2][1],
            }
        }, mask_json)


stdscr = curses.initscr()
curses.noecho()
curses.cbreak()
stdscr.keypad(True)
curses.start_color()

# Initialise curses color pairs for the program
curses.init_pair(1, curses.COLOR_GREEN, curses.COLOR_BLACK)
curses.init_pair(2, 163, curses.COLOR_BLACK)
curses.init_pair(3, 105, curses.COLOR_BLACK)

# 10-12 is the slider ranges
curses.init_pair(10, 48, curses.COLOR_BLACK)
curses.init_pair(11, 105, curses.COLOR_BLACK)
curses.init_pair(12, 135, curses.COLOR_BLACK)

# Run the program
main(stdscr)

curses.nocbreak()
stdscr.keypad(False)
curses.echo()
curses.endwin()

mask_json.close()

