import numpy as np
import cv2
import math
import os
import projection_math

cap = cv2.VideoCapture(2)


K=np.array([[678.3675545820577, 0.0, 304.74552960651096], 
            [0.0, 677.88787206697, 228.7902426460552], 
            [0.0, 0.0, 1.0]])

R = projection_math.rotation_matrix(0, -14, 0, single_axis="X") # ROTATION

# # paired_points = np.array([
# #     [
# #         [383,206,1],
# #         [3,46,54]
# #     ],
# #     [
# #         [575,196,1],
# #         [24, 42.5, 54]
# #     ]
# # ])

# obj_points = np.array([
#     [0, 0, 0],
#     [-4.5, 15.125, 4.25],
#     [-3.5, 15.13, 4.25],
#     [-1.5, 15.25, 4.25],
#     [2, 16.125, 4.25],
#     [2.55, 16.75, 4.25],
#     [4.25, 17.75, 4.25]
# ])

# img_points = np.array([
#     [336, 236, 1],
#     [75, 51, 1],
#     [157, 55, 1],
#     [271, 65, 1],
#     [374, 73, 1],
#     [439, 78, 1],
#     [507, 83, 1]
# ])

# paired_points = np.zeros([7, 2, 3])

# sum_err_x = []
# sum_err_y = []
# sum_err_z = []

# for i in range(7):
#     u, v, _ = img_points[i]
#     real_x, real_y, real_z = obj_points[i] # ACCEPTED

#     calc_x, calc_y, calc_z = projection_math.similar_triangles_calculation(u, v, K) # EXPERIMENTAL

#     if real_x == 0 or real_y == 0 or real_z == 0:
#         continue

#     err_x = abs(real_x - calc_x) / real_x
#     err_y = abs(real_y - calc_y) / real_y
#     err_z = abs(real_z - calc_z) / real_z

#     # print(f"Loop {i}:   X[{err_x*100}%]  Y[{err_y*100}%]  Z[{err_z*100}%]")
#     print(f"Loop: {i}: X REAL[ {real_x} ]   X CALC[ {calc_x} ]")

#     sum_err_x.append(err_x)
#     sum_err_y.append(err_y)
#     sum_err_z.append(err_z)

# avg_err_x = sum(sum_err_x) / len(sum_err_x)
# avg_err_y = sum(sum_err_y) / len(sum_err_y)
# avg_err_z = sum(sum_err_z) / len(sum_err_z)

# print(f"\nAverage Errors:    X[{avg_err_x*100}%]    Y[{avg_err_y*100}%]    Z[{avg_err_z*100}%]")


# exit()

# # s = projection_math.calibrate_s(paired_points, K, R)
# u, v = (507, 83)
# # x, y, z = projection_math.camera_to_world(u, v, K, R, s)
# x, y, z = projection_math.similar_triangles_calculation(u, v, K)

# print("|------------------ FINAL RESULTS ------------------|")
# print(f"ROTATION:\n{R}\n")
# print(f"INTRINSIC:\n{K}\n")
# # print(f"S: {s}\n")
# print(f"IMG:   ({u}, {v})")
# print(f"WORLD: ({x}, {y}, {z})")
# print("|---------------------------------------------------|")
# exit()

hue = [25, 90]    #'''DONT TOUCH'''
sat = [75, 255]    #'''DONT TOUCH'''
lum = [35, 150]    #'''DONT TOUCH'''

dil_iters = 3
erode_iters = 3

cap.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
cap.set(cv2.CAP_PROP_EXPOSURE, -15)

relative_z = 104-22.25 #inches

while True:
    # ret, img = cap.read()
    img = cv2.imread("test_today.png")
    # cv2.imwrite("test_today.png", img)


    hls_img = cv2.cvtColor(img, cv2.COLOR_BGR2HLS)
    bw_img = cv2.inRange(hls_img, (hue[0], lum[0], sat[0]),  (hue[1], lum[1], sat[1]))

    #Dilate areas of high val then close holes
    img_erode = cv2.erode(bw_img, None, erode_iters)
    img_dilate = cv2.dilate(img_erode, None, dil_iters)
    img_closing = cv2.morphologyEx(img_dilate, cv2.MORPH_CLOSE, None)

    #Find contours
    contours, hierarchy = cv2.findContours(img_closing, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
    c_cnt = 0

    target_point_xs = []
    target_point_ys = []

    for c in contours:
        x, y, w, h = cv2.boundingRect(c)

        if w > 5 and h > 5:

            x += w // 2
            y += h // 2

            target_point_xs.append(x)
            target_point_ys.append(y)

            cv2.circle(img, (x,y), 3, (0,0,255), thickness=-1)

            c_cnt += 1

    if len(target_point_xs) >= 2:
        p1 = (target_point_xs[0], target_point_ys[0])
        p2 = (target_point_xs[1], target_point_ys[1])

        x1,y1 = p1
        x2,y2 = p2

        print(projection_math.similar_triangles_calculation(x1, y1, K, R))
        print(projection_math.similar_triangles_calculation(x2, y2, K, R))


        cv2.circle(img, p1, 3, (0,255,0), thickness=-1)
        cv2.circle(img, p2, 3, (0,255,0), thickness=-1)

        angle, distance, _, _ = projection_math.geometric_true_center(p1, p2, K, R)

        print(f"Angle[ {angle} ]   Distance[ {distance} ]")


    cv2.imshow('frame', img)

    # os.system('cls')
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()