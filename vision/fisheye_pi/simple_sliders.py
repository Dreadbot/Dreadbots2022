import json
from pynput import keyboard
import termios
import sys
import os

selectionI = 0
monitors: dict = {}

def enable_echo(enable):
    try:
        fd = sys.stdin.fileno()
        new = termios.tcgetattr(fd)
        if enable:
            new[3] |= termios.ECHO
        else:
            new[3] &= ~termios.ECHO

        termios.tcsetattr(fd, termios.TCSANOW, new)
    except:
        pass


def safe_exit():
    enable_echo(True)
    # os.system("clear")
    quit(0)


def send_info():
    os.system("clear")
    print(get_monitor_string())


# def send_range_info(range: range_manager.Range):
#     send_info(f"L1: {range.lower[0]}\nL2: {range.lower[1]}\nL3: {range.lower[2]}\nU1: {range.upper[0]}\nU2: {range.upper[1]}\nU3: {range.upper[2]}")


def get_data_file(mode):
    return open(os.path.join(os.path.dirname(os.path.realpath(__file__)), "slider_monitors.json"), mode)


def get_monitor_string():
    global selectionI
    global monitors

    finalS = ""

    for k in monitors:
        kS = f"{k}: {monitors[k]['value']}\n"
        if list(monitors).index(k) == selectionI:
            kS = "> " + kS
        
        finalS += kS

    return(finalS)

def main():
    global selectionI
    global monitors
    selectionI = 0
    monitors = json.load(get_data_file("r"))

    enable_echo(False)

    send_info()

    def on_press(key):
        global selectionI
        global monitors

        currentM = monitors[list(monitors)[selectionI]]

        if key == keyboard.Key.down and selectionI < len(monitors.keys()) - 1:
            selectionI += 1
        elif key == keyboard.Key.up and selectionI > 0:
            selectionI -= 1
        elif key == keyboard.Key.right and currentM["value"] < currentM["upper"]:
            currentM["value"] += 1
            json.dump(monitors, get_data_file("w"), indent=4)
        elif key == keyboard.Key.left and currentM["value"] > currentM["lower"]:
            currentM["value"] -= 1
            json.dump(monitors, get_data_file("w"), indent=4)
        elif key == keyboard.Key.esc:
            safe_exit()
        else:
            return

        send_info()
            

    with keyboard.Listener(
            on_press=on_press) as listener:
        listener.join()


if __name__ == "__main__":
    try:
        main()
    except:
        safe_exit()