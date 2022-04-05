import cv2
import range_manager
from cscore import CameraServer
from networktables import NetworkTables


def main():
    r = range_manager.Range("e", (1,1,1), (2,2,2))
    range_manager.store_range_object(r)

    newR = range_manager.get_range("e")
    print(newR.lower)


if __name__ == "__main__":
    main()
