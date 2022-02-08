// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

public abstract class Constants {
    public static final boolean DRIVE_ENABLED = true;
    public static final boolean INTAKE_ENABLED = true;
    public static final boolean SHOOTER_ENABLED = false;
    public static final boolean CLIMB_ENABLED = false;
    public static final boolean FEEDER_ENABLED = true;

    // Joysticks
    public static final int PRIMARY_JOYSTICK_PORT = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 1;

    // Drive Ports
    public static final int LEFT_FRONT_DRIVE_MOTOR_PORT = 10;
    public static final int RIGHT_FRONT_DRIVE_MOTOR_PORT = 11;
    public static final int LEFT_BACK_DRIVE_MOTOR_PORT = 12;
    public static final int RIGHT_BACK_DRIVE_MOTOR_PORT = 13;

    // Intake Ports
    public static final int INTAKE_MOTOR_PORT = 14;
    public static final double INTAKE_MOTOR_RATIO = 1/12;

    // Shooter Ports
    public static final int FLYWHEEL_MOTOR_PORT = 15;
    public static final int HOOD_MOTOR_PORT = 16;
    public static final int TURRET_MOTOR_PORT = 17;

    public static final double FLYWHEEL_P_GAIN = 1e-4d;
    public static final double FLYWHEEL_I_GAIN = 0.0d;
    public static final double FLYWHEEL_D_GAIN = 0.0d;
    public static final double FLYWHEEL_I_ZONE = 2.0d;
    public static final double FLYWHEEL_FF_GAIN = 1.9e-4d;
    public static final double FLYWHEEL_MAX_OUTPUT = 1.0d;
    public static final double FLYWHEEL_MIN_OUTPUT = -1.0d;
    public static final double FLYWHEEL_MAX_RPM = 5700.0d;
    
    // Climber constants
    public static final int WINCH_MOTOR_PORT = 15;
    public static final int LEFT_NEUTRAL_HOOK_ACTUATOR = 1;
    public static final int RIGHT_NEUTRAL_HOOK_ACTUATOR = 2;
    public static final int CLIMBING_HOOK_ACTUATOR = 3;
    public static final double MAX_ARM_DISTANCE = 100; //Later change this to phyisical distance
    // Robot Constants
    public static final float GRAVITY = -9.81f;
    public static final float GOAL_HEIGHT = 2.64f;
    public static final float INITIAL_BALL_HEIGHT = 0.4f;

    public static final double TO_RPM = 4 * 60 / 0.279d;
}
