import curses
import math

def main(stdscr):
    # Hide the cursor
    curses.curs_set(0)
    # Start colour mode in the terminal
    curses.start_color()
    
    # Initialise slider arrays`
    sliders = [[0, 255, 0, 255, "H"], [0, 255, 0, 255, "S"], [0, 255, 0, 255, "V"]]
    # Initialise status variables
    cur_slider = 0
    left = True
    
    # Constant that is the length (in chars) of the slider bar
    slider_length = 50

    # Initialise curses color pairs for the program
    curses.init_pair(1, curses.COLOR_GREEN, curses.COLOR_BLACK)
    curses.init_pair(2, 163, curses.COLOR_BLACK)
    curses.init_pair(3, 105, curses.COLOR_BLACK)

    # 10-12 is the slider ranges
    curses.init_pair(10, 48, curses.COLOR_BLACK)
    curses.init_pair(11, 105, curses.COLOR_BLACK)
    curses.init_pair(12, 135, curses.COLOR_BLACK)
    

    while True:
        # Clear the screen every frame
        stdscr.clear()

        # Number of sliders
        length = len(sliders)

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
            break # Exit the program
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
        elif key == curses.KEY_UP:
            # Select the next slider up, but don't go over
            cur_slider = max(cur_slider - 1, 0)

# Start the program running
curses.wrapper(main)

