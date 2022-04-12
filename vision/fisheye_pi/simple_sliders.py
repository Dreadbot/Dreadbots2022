from pynput import keyboard
import termios
import sys
import range_manager
import os

selectionI = 0

def enable_echo(enable):
    fd = sys.stdin.fileno()
    new = termios.tcgetattr(fd)
    if enable:
        new[3] |= termios.ECHO
    else:
        new[3] &= ~termios.ECHO

    termios.tcsetattr(fd, termios.TCSANOW, new)


def safe_exit(range):
    if range is not None: range_manager.store_range_object(range)

    enable_echo(True)
    exit()


def send_info(string: str):
    os.system("clear")
    print(string)


# def send_range_info(range: range_manager.Range):
#     send_info(f"L1: {range.lower[0]}\nL2: {range.lower[1]}\nL3: {range.lower[2]}\nU1: {range.upper[0]}\nU2: {range.upper[1]}\nU3: {range.upper[2]}")


def main():
    global selectionI
    selectionI = 0

    rangeName = input("Range to Edit: " )
    r = range_manager.get_range(rangeName)

    if r is None:
        r = range_manager.Range(rangeName, (0, 0, 0), (255, 255, 255))

    enable_echo(False)

    send_info(selectionI)

    def on_press(key):
        global selectionI

        try:
            if key == keyboard.Key.up:
                selectionI += 1
            elif key == keyboard.Key.down:
                selectionI -= 1
            elif key == keyboard.Key.esc:
                safe_exit(r)

            send_info(selectionI)
        except AttributeError:
            pass

    with keyboard.Listener(
            on_press=on_press) as listener:
        listener.join()


if __name__ == "__main__":
    try:
        main()
    except:
        safe_exit(None)