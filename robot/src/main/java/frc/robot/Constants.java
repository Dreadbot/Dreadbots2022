// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.util.Color;

@SuppressWarnings("SpellCheckingInspection")
public abstract class Constants {
    //region * SECTION - CONTROL PANEL
    public static final boolean DRIVE_ENABLED        = true;
    public static final boolean INTAKE_ENABLED       = true;
    public static final boolean CLIMB_ENABLED        = true;
    public static final boolean SHOOTER_ENABLED      = true;
    public static final boolean FEEDER_ENABLED       = true;
    public static final boolean FLYWHEEL_ENABLED     = true;
    public static final boolean HOOD_ENABLED         = true;
    public static final boolean TURRET_ENABLED       = true;
    public static final boolean COLOR_SENSOR_ENABLED = true;
    //endregion

    //region * SECTION - ROBOT PORTS
    // =================================================================================================================

    // Joystick Ports
    public static final int PRIMARY_JOYSTICK_PORT   = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 1;

    // Motor Ports
    public static final int LEFT_FRONT_DRIVE_MOTOR_PORT  = 1;
    public static final int RIGHT_FRONT_DRIVE_MOTOR_PORT = 2;
    public static final int LEFT_BACK_DRIVE_MOTOR_PORT   = 3;
    public static final int RIGHT_BACK_DRIVE_MOTOR_PORT  = 4;
    public static final int INTAKE_MOTOR_PORT            = 5;
    public static final int FEEDER_MOTOR_PORT            = 6;
    public static final int TURRET_MOTOR_PORT            = 7;
    public static final int FLYWHEEL_MOTOR_PORT          = 8;
    public static final int HOOD_MOTOR_PORT              = 9;
    public static final int WINCH_MOTOR_PORT             = 10;

    // Limit Switch Ports
    public static final int LOWER_TURRET_LIMIT_SWITCH_ID = 0;
    public static final int UPPER_TURRET_LIMIT_SWITCH_ID = 1;
    public static final int LOWER_HOOD_LIMIT_SWITCH_ID   = 2;
    public static final int UPPER_HOOD_LIMIT_SWITCH_ID   = 3;
    public static final int CLIMBER_LIMIT_SWITCH_ID      = 4;

    // Solenoid Ports
    public static final int NEUTRAL_HOOK_SOLENOID_ID = 1;
    public static final int POWER_HOOK_SOLENOID_ID   = 0;

    // Other Sensor Ports
    public static final I2C.Port        I2C_PORT       = I2C.Port.kOnboard;
    public static final SerialPort.Port GYROSCOPE_PORT = SerialPort.Port.kUSB;
    //endregion

    //region * SECTION - DRIVE CONSTANTS
    // =================================================================================================================

    // Drive Controller Constants
    public static final double TRAJECTORY_ERROR_CONTROLLER_P = 3.6109d;
    public static final double WHEEL_CONTROLLER_P            = 1.0d;

    // Drive Mechanical Wheel Specifications
    public static final double WHEEL_GEARING                  = 14.0d / 70.0d;
    public static final double WHEEL_CIRCUMFERENCE_METERS     = Units.inchesToMeters(3.0d) * 2 * Math.PI;
    public static final double WHEEL_ROTATIONS_TO_METERS      = WHEEL_CIRCUMFERENCE_METERS * WHEEL_GEARING;
    public static final double WHEEL_RPM_TO_METERS_PER_SECOND = WHEEL_ROTATIONS_TO_METERS / 60.0d;

    // This is how far the wheel centers are from the physical center of the robot
    // Longitudinal (forward/backward) and Lateral (side/side) distances are useful for kinematics.
    public static final double WHEEL_LONGITUDINAL_DISPLACEMENT = 0.19d;   // meters
    public static final double WHEEL_LATERAL_DISPLACEMENT      = 0.4191d; // meters

    // These are the individual wheel feedforward gains
    public static final double WHEEL_FEED_STATIC_FRICTION_GAIN = 0.09185d;
    public static final double WHEEL_FEED_VELOCITY_GAIN        = 3.1899d;
    public static final double WHEEL_FEED_ACCELERATION_GAIN    = 0.17172d;
    //endregion

    //region * SECTION - INTAKE CONSTANTS
    // =================================================================================================================

    // Intake Constants
    public static final double INTAKE_INTAKING_MAX_POWER  = 1.0d;
    public static final double INTAKE_OUTTAKING_MAX_POWER = -1.0d;
    //endregion

    //region * SECTION - TURRET CONSTANTS
    // =================================================================================================================

    // Turret Constants
    public static final double MAX_TURRET_ANGLE  = 300.0d;
    public static final double MIN_TURRET_ANGLE  = 0.0d;
    public static final double TURRET_RANGE      = 76.24d;//OLD VALUE 56.452d
    public static final double TURRET_SPIN_SPEED = 0.25d; //0.2d
    //endregion

    //region * SECTION - FLYWHEEL CONSTANTS
    // =================================================================================================================

    // Flywheel Constants
//    public static final double FLYWHEEL_P_GAIN     = 2e-4d; // 1e-4
    public static final double FLYWHEEL_P_GAIN     = 3e-4d; // 1e-4
    public static final double FLYWHEEL_I_GAIN     = 1.75e-6d; //1.75e-6d
    public static final double FLYWHEEL_D_GAIN     = 0.0d;
    public static final double FLYWHEEL_I_ZONE     = 2.0d;
    public static final double FLYWHEEL_FF_GAIN    = 2.5e-4d; //2.5-4d
    public static final double FLYWHEEL_MAX_OUTPUT = 1.0d;
    public static final double FLYWHEEL_MIN_OUTPUT = -1.0d;
    public static final double FLYWHEEL_MAX_RPM    = 5700.0d;
    //endregion

    //region * SECTION - HOOD CONSTANTS
    // =================================================================================================================

    // Hood Constants
    public static final double MAX_HOOD_ANGLE         = 75.2d;
    public static final double UPPER_HOOD_ANGLE       = 70.0d;
    public static final double MIN_HOOD_ANGLE         = 57.0d;
    public static final double HOOD_RANGE             = 77.07d;
    public static final double HOOD_CALIBRATION_SPEED = 0.2d;
    //endregion

    //region * SECTION - COLOR SENSOR CONSTANTS
    // =================================================================================================================

    // Color Sensor Constants
    public static final Color COLOR_BLUE = new Color(.17, .42, .41); // May need to be tweaked later
    public static final Color COLOR_RED  = new Color(.58, .33, .09);
    //endregion

    //region * SECTION - CLIMBER CONSTANTS
    // =================================================================================================================

    // Climber Constants
    public static final double MAX_ARM_DISTANCE     = 50; //Later change this to phyisical distance
    public static final double CLIMBER_RANGE        = 205.0d; // 60.5 in
    public static final double MEDIUM_CLIMBER_RANGE        = 165.0d; // 60.5 in
    public static final double NEUTRAL_CLIMBER_ROLL = 10.00d; // degrees
    //endregion

    //region * SECTION - ROBOT CONSTANTS
    // =================================================================================================================

    // Robot Constants
    public static final double GRAVITY             = -9.81d;
    public static final double GOAL_HEIGHT         = 2.64f;
    public static final double INITIAL_BALL_HEIGHT = 0.2d;
    //endregion

    private Constants() throws IllegalStateException {
        throw new IllegalStateException("Constants is a utility class. It should not be instantiated.");
    }
}
