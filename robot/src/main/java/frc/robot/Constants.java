// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

public abstract class Constants {
    public static final boolean DRIVE_ENABLED = true;
    public static final boolean INTAKE_ENABLED = true;
    public static final boolean SHOOTER_ENABLED = true;
    public static final boolean CLIMB_ENABLED = true;

    // Joysticks
    public static final int PRIMARY_JOYSTICK_PORT = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 1;

    // Drive Ports
    public static final int LEFT_FRONT_DRIVE_MOTOR_PORT = 10;
    public static final int RIGHT_FRONT_DRIVE_MOTOR_PORT = 11;
    public static final int LEFT_BACK_DRIVE_MOTOR_PORT = 12;
    public static final int RIGHT_BACK_DRIVE_MOTOR_PORT = 13;

    // Intake Ports
    public static final int LEFT_INTAKE_MOTOR_PORT = 14;
    public static final int RIGHT_INTAKE_MOTOR_PORT = 15;

    // Shooter Ports
    public static final int FLYWHEEL_MOTOR_PORT = 16;
    public static final int HOOD_MOTOR_PORT = 17;
    public static final int TURRET_MOTOR_PORT = 18;

    // Climber constants
    public static final int LEFT_WINCH_MOTOR_PORT = 19;
    public static final int RIGHT_WINCH_MOTOR_PORT = 20;
    public static final int LEFT_NEUTRAL_HOOK_ACTUATOR = 21;
    public static final int RIGHT_NEUTRAL_HOOK_ACTUATOR = 22;
    public static final int LEFT_CLIMBING_HOOK_ACTUATOR = 23;
    public static final int RIGHT_CLIMBING_HOOK_ACTUATOR = 24;

    // Robot Constants
    public static final float GRAVITY = -9.81f;
    public static final float GOAL_HEIGHT = 2.64f;
    public static final float INITIAL_BALL_HEIGHT = 0.4f;

    public static final double TO_RPM = 4 * 60 / 0.279d;
}
