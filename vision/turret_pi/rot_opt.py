import numpy as np
from scipy.optimize import minimize
import projection_math
import math

imgpts = [
    [315,12],
    [322,67],
    [315,113],
    [322,149],
    [324,177],
    [319,207],
    [323,226],
    [323,239],
    [323,264],
    [319,296],
    [318,314],
    [320,332],
    [323,353],
    [210,224],
    [212,206],
    [194,173],
    [177,144],
    [169,111],
    [153,68],
    [119,11]]


realpts = [
    [0,73.5,66.5],
    [0,85,66.5],
    [0,97.5,66.5],
    [0,110,66.5],
    [0,121.5,66.5],
    [0,133.5,66.5],
    [0,146,66.5],
    [0,157.25,66.5],
    [0,169,66.5],
    [0,192.25,66.5],
    [0,216.75,66.5],
    [0,240,66.5],
    [0,265.5,66.5],
    [-24,143.75,66.5],
    [-24,131.25,66.5],
    [-24,118.25,66.5],
    [-24,107.25,66.5],
    [-24,95,66.5],
    [-24,83.25,66.5],
    [-24,71.5,66.5]]




def synthetic_rot(x):
    yaw, pitch, roll = x

    forced_pitch = 40

    R = projection_math.rotation_matrix(yaw, pitch, roll)

    n = len(imgpts)

    sum_norm = 0

    for i in range(n):
        real_u, real_v = imgpts[i]
        if real_v > 240:
            break

        theta = math.degrees(math.atan( abs(real_v - 240) / 554 ))

        real_y = 66.5/math.tan(math.radians(theta+forced_pitch))

        calc_vec = projection_math.similar_triangles_calculation(320, imgpts[i][1], R=R)

        real_vec = [0, real_y, 66.5]

        sum_norm += np.linalg.norm(real_vec-calc_vec)

    return(sum_norm)

def rot_err(x):
    yaw, pitch, roll = x
    R = projection_math.rotation_matrix(yaw, pitch, roll)

    n = len(realpts)

    sum_norm = 0

    for i in range(n):
        real_vec = np.array([realpts[i]])
        u, v = imgpts[i]


        calc_vec = projection_math.similar_triangles_calculation(u, v, R=R)
        # print(f"Calculated[{calc_vec}]")
        cur_error = np.linalg.norm(real_vec-calc_vec)
        # print(cur_error)
        sum_norm += cur_error

    return(sum_norm)

x0 = np.array([0, 24, 0])

res = minimize(rot_err, x0, method="BFGS", tol=1)
print(res)

