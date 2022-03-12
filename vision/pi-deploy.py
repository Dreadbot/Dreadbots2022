import os
import sys
import getopt

help_msg = '''
2022 Pi Publisher/Puller
-h : Display this message           |
-t : Target Pi <turret/fisheye>     | --target
-w : Write local code to target Pi  |
-r : Pull current Pi code           |
'''

def main(argv):
    target_pi = ''
    read_write = True # True - READ   False - WRITE

    ip = ''

    target = ''

    src = ''

    local_path = ''

    try:
        opts, args = getopt.getopt(argv, "h:t:w:r", ["target="])
    except getopt.GetoptError:
        print(help_msg)
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print(help_msg)
            sys.exit()
        elif opt in ("-t", "--target") and arg in ('turret', 'fisheye'):
            target_pi = arg
        elif opt == "-w":
            read_write = False
        elif opt == '-r':
            read_write = True
        
    if target_pi == 'turret':
        ip = '10.36.56.11'
    elif target_pi == 'fisheye':
        ip = '10.36.56.12'

    if read_write:
        target = target_pi
        src = 

if __name__ == "__main__":
    main(sys.argv[1:])