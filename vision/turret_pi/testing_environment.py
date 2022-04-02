import numpy as np
import projection_math


target_vec = np.array([-15, 105, 66.5])

output_Rs = []

def rotate_test(target_axis, step=0.5):
    yaw = 0
    pitch = 0
    roll = 0

    while True:
        if 'Z' in target_axis:
            yaw += step
        if 'X' in target_axis:
            pitch += step
        if 'Y' in target_axis:
            roll += step
            
        thread_R = projection_math.rotation_matrix(yaw,pitch,roll)



wvec = projection_math.similar_triangles_calculation(320, 75)
print(wvec)