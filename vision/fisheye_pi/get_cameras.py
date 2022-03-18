import cv2


def main():
    cams = []

    for i in range(10):
        try:
            cam = cv2.VideoCapture(i)
            cams.append(f"Cam {i}: {cam.getBackendName()}")
        except:
            continue

    print("Cameras found at: ")
    for c in cams:
        print(c)

if __name__ == "__main__":
    main()
