import cv2
import util
import circularity
import hough


def main():
    rangeName = input("Range: ")

    util.setupDefaultSliderWindow("hsv", "Trackbars", rangeName)

    vc = cv2.VideoCapture(1)
    vc.set(cv2.CAP_PROP_EXPOSURE, -4)

    while True:
        ret, frame = vc.read()

        if not ret:
            break

        hL, sL, vL, hU, sU, vU, erode, dilate, blur, minArea, circ = util.getSliderValues(
            "hsv", "Trackbars")

        lower = (hL, sL, vL)
        upper = (hU, sU, vU)
        minCirc = circ / 100

        mask = util.getMask(frame, lower, upper, erode, dilate, blur)
        circle = circularity.getBall(mask, minCirc, minArea)
        h = hough.getBall(mask)
        circles = []
        if circle is not None:
            circles.append((circle[0], circle[1], circle[2], "Circularity"))

        if h is not None:
            circles.append((h[0], h[1], h[2], "Hough"))

        # print(circles)

        for circle in circles:
            newF = frame.copy()
            distance, angle = util.getDistance(
                mask, circle[0], circle[2], util.focalLength, util.ballDiameterI)
            cv2.circle(newF, (int(circle[0]), int(circle[1])),
                       int(circle[2]), (255, 255, 0), 2)
            cv2.putText(newF, f"d: {distance}", (0, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            cv2.putText(newF, f"0: {angle}", (0, 60),
                        cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)

            cv2.imshow(circle[3], newF)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            util.updateLiveRange(rangeName, (hL, sL, vL), (hU, sU, vU))

            util.setAllManipulation(erode, dilate, blur, minArea, circ)
            break

    vc.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
