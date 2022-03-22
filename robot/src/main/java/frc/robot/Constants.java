// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public abstract class Constants {
    public static final boolean DRIVE_ENABLED = true;
    public static final boolean INTAKE_ENABLED = true;
    public static final boolean CLIMB_ENABLED = true;
    public static final boolean SHOOTER_ENABLED = true;
    public static final boolean FEEDER_ENABLED = true;
    public static final boolean FLYWHEEL_ENABLED = true;
    public static final boolean HOOD_ENABLED = true;
    public static final boolean TURRET_ENABLED = true;
    public static final boolean COLOR_SENSOR_ENABLED = true;

    // Joysticks
    public static final int PRIMARY_JOYSTICK_PORT = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 1;

    // Drive Ports
    public static final int LEFT_FRONT_DRIVE_MOTOR_PORT = 1;
    public static final int RIGHT_FRONT_DRIVE_MOTOR_PORT = 2;
    public static final int LEFT_BACK_DRIVE_MOTOR_PORT = 3;
    public static final int RIGHT_BACK_DRIVE_MOTOR_PORT = 4;

    // Intake Ports
    public static final int INTAKE_MOTOR_PORT = 5;

    // Shooter Ports
    public static final int FEEDER_MOTOR_PORT = 6;

    public static final int TURRET_MOTOR_PORT = 7;
    public static final int LOWER_TURRET_LIMIT_SWITCH_ID = 0;
    public static final int UPPER_TURRET_LIMIT_SWITCH_ID = 1;
    public static final double MAX_TURRET_ANGLE = 300.0d;
    public static final double MIN_TURRET_ANGLE = 0.0d;
    public static final double TURRET_RANGE = 33.61d;//OLD VALUE 56.452d
    public static final double TURRET_CALIBRATION_SPEED = 0.2d;

    public static final int FLYWHEEL_MOTOR_PORT = 8;
//    public static final double FLYWHEEL_P_GAIN = 2e-3d; // 1e-4
    public static final double FLYWHEEL_P_GAIN = 1e-5d; // 1e-4
    public static final double FLYWHEEL_I_GAIN = 1.75e-6d;
    public static final double FLYWHEEL_D_GAIN = 0.0d;
    public static final double FLYWHEEL_I_ZONE = 0.0d;
    public static final double FLYWHEEL_FF_GAIN = 1.5e-4d;
    public static final double FLYWHEEL_MAX_OUTPUT = 1.0d;
    public static final double FLYWHEEL_MIN_OUTPUT = -1.0d;
    public static final double FLYWHEEL_MAX_RPM = 5700.0d;

    public static final int HOOD_MOTOR_PORT = 9;
    public static final int LOWER_HOOD_LIMIT_SWITCH_ID = 2;
    public static final int UPPER_HOOD_LIMIT_SWITCH_ID = 3;
    public static final double MAX_HOOD_ANGLE = 75.2d;
    public static final double UPPER_HOOD_ANGLE = 70.0d;
    public static final double MIN_HOOD_ANGLE = 57.0d;
    public static final double HOOD_RANGE = 77.07d;
    public static final double HOOD_CALIBRATION_SPEED = 0.2d;
    
    // Climber constants
    public static final int WINCH_MOTOR_PORT = 10;
    public static final int NEUTRAL_HOOK_ACTUATOR_ID = 0;
    public static final int CLIMBING_HOOK_ACTUATOR_ID = 1;
    public static final int CLIMBER_LIMIT_SWITCH_ID = 4;
    public static final double MAX_ARM_DISTANCE = 50; //Later change this to phyisical distance
    public static final double CLIMBER_RANGE = 135.0d;

    // Robot Constants
    public static final double GRAVITY = -9.81d;
    public static final double GOAL_HEIGHT = 2.64f;
    public static final double INITIAL_BALL_HEIGHT = 0.2d;

    public static final double TO_RPM = 4 * 60 / 0.279d;

    //Color Sensor
    public static final I2C.Port I2C_PORT = I2C.Port.kOnboard;
    public static final Color COLOR_BLUE =  new Color(.17, .42, .41); // May need to be tweaked later
    public static final Color COLOR_RED = new Color(.58, .33, .09);

    private Constants() {
        throw new IllegalStateException("The Constants class is a utility class. It should not be instantiated.");
    }
}
