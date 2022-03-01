import curses
from curses import wrapper
import random
import json
from networktables import NetworkTables
import time, sys, logging       

vchar = None
hchar = None

log = ""

def quick_log(input_str):
    global log

    log += "\n"+input_str

def init_curses(screen):
    global vchar
    global hchar

    screen.clear()
    screen.nodelay(1)
    screen.keypad(1)

    curses.noecho()
    curses.use_default_colors()
    curses.curs_set(1)

    vchar = curses.ACS_VLINE
    hchar = curses.ACS_HLINE


def dividers(screen, start_x, stop_x, stop_y, output_width, padding):
    start_x = start_x
    stop_y = stop_y
    stop_x = stop_x
    output_width = output_width
    padding = padding

    screen.vline(0, 0, vchar, stop_y)
    screen.vline(0, start_x, vchar, stop_y)
    screen.vline(0, start_x+output_width+padding, vchar, stop_y)
    screen.vline(0, stop_x, vchar, stop_y)
    screen.hline(0, 0, hchar, stop_x)


def show_monitors(screen, table, monitors, start_y, start_x, stop_x, padding, output_width):
    for title in monitors:
            monitor_type = monitors[title]['type']
            monitor_default = monitors[title]['default']

            if monitor_type == 'num' or monitor_type == 'number':
                value = table.getNumber(title, monitor_default)
            else:
                value = ""

            value = str(value)[0:output_width]
            screen.addstr(start_y, padding+1, title)
            screen.addstr(start_y, start_x+padding, value)
            screen.hline(start_y+1, 0, hchar, stop_x)

            start_y += 2


def nt_update(table, selection, monitors, buffer):
    monitor_type = monitors[selection]['type']
    if monitor_type == 'num' or monitor_type == 'number':
        table.putNumber(selection, buffer)


def nt_connect():
    #    logging.basicConfig(level=logging.DEBUG)

    ip = "10.36.56.2"

    NetworkTables.initialize(server=ip)

    def connection_listener(connected, info):
        #print(info, "; connected=%s" % connected)
        pass

    NetworkTables.addConnectionListener(connection_listener, immediateNotify=True)

    table = NetworkTables.getTable('SmartDashboard')

    return table


def main(screen):
    init_curses(screen)
    table = nt_connect()

    h, w = screen.getmaxyx()

    

    with open("monitors.json", 'r') as f:
        monitors = json.load(f)


    longest_monitor = max([len(x) for x in monitors])

    output_width = 8 #also truncation width
    input_width = 5


    padding = 2

    start_y = 1
    start_x = longest_monitor + 2*padding

    my, mx = (1, start_x+output_width+padding+1)

    start_y = 1
    start_x = longest_monitor + 2*padding

    stop_x = start_x + output_width + padding + input_width + padding
    stop_y = 1 + len(monitors)*2

    buffer = []

    while True:
        screen.erase()

        dividers(screen, start_x, stop_x, stop_y, output_width, padding)

        show_monitors(screen, table, monitors, start_y, start_x, stop_x, padding, output_width)
        
        cursor_output = ''.join(map(str, buffer))
        screen.addstr(my, mx, cursor_output)

        screen.move(my, mx+len(cursor_output))

        screen.refresh()
        
        key = screen.getch()

        if key == curses.KEY_DOWN:
            my = min([my+2, stop_y-2])
        elif key == curses.KEY_UP:
            my = max([my-2, 1])
        elif key == ord('q'):
            break
        elif key == curses.KEY_ENTER or key == ord('\n'):
            selection_index = (my-1) // 2
            selection = list(monitors)[selection_index]
            quick_log(str(type(selection)))
            buffer = float(''.join(map(str, buffer)))
            nt_update(table, selection, monitors, buffer)
            buffer = []
        else:
            if key is not curses.ERR and chr(key).isalnum():
                buffer.append(chr(key))


wrapper(main)
print(log)
