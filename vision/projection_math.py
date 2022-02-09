import numpy as np
import math


def rotation_matrix(yaw, pitch, roll):
    z_rotation_matrix = np.array([
        [math.cos(yaw), -math.sin(yaw), 0],
        [math.sin(yaw), math.cos(yaw), 0],
        [0, 0, 1]
    ])

    x_rotation_matrix = np.array([
        [1, 0, 0],
        [0, math.cos(pitch), math.sin(pitch)],
        [0, math.sin(pitch), math.cos(pitch)]
    ])

    y_rotation_matrix = np.array([
        [math.cos(roll), 0, -math.sin(roll)],
        [0, 1, 0],
        [math.sin(roll), 0, math.cos(roll)]
    ])

    zx_multiplied = np.matmul(z_rotation_matrix, x_rotation_matrix)
    output_rotation_matrix = np.matmul(zx_multiplied, y_rotation_matrix)

    return output_rotation_matrix

def calibrate_s(paired_points, intrinsic_matrix, rotation_matrix):
    raw_s_results = []

    intrinsic_rotation = np.matmul(intrinsic_matrix, rotation_matrix)

    for pair in paired_points:
        image_coord_vector = pair[0]
        world_coord_vector = pair[1]

        u, v, _ = image_coord_vector
        x, y, z = world_coord_vector

        resultant_vector = intrinsic_rotation.dot(world_coord_vector)

        resultant_x, resultant_y, resultant_z = resultant_vector

        s_x = resultant_x/u
        s_y = resultant_y/v
        s_  = resultant_z

        avg_s = sum([s_x, s_y, s_])/3

        raw_s_results.append(avg_s)


    calibrated_s = sum(raw_s_results)/len(raw_s_results)
    
    return calibrated_s


'''
paired_points = np.array([
    [
        [u,v,1], <- IMAGE COORDS
        [x,y,z]  <- WORLD COORDS
    ]
])
'''        