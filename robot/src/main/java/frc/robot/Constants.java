// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

public abstract class Constants {
    /**
     * Motor Ports
     */
    public static final int PRIMARY_JOYSTICK_PORT = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 1;

    public static final int LEFT_FRONT_DRIVE_MOTOR_PORT = 10;
    public static final int RIGHT_FRONT_DRIVE_MOTOR_PORT = 1;
    public static final int LEFT_BACK_DRIVE_MOTOR_PORT = 2;
    public static final int RIGHT_BACK_DRIVE_MOTOR_PORT = 3;

    public static final int LEFT_INTAKE_MOTOR_PORT = 5;
    public static final int RIGHT_INTAKE_MOTOR_PORT = 6;

    public static final int FLYWHEEL_MOTOR_PORT = 1;
    public static final int HOOD_MOTOR_PORT = 3;
    public static final int TURRET_MOTOR_PORT = 2;
    /**
     * Shooter Constants
     */
    public static final float GRAVITY = -9.81f;
    public static final float GOAL_HEIGHT = 2.64f;
    public static final float INITIAL_BALL_HEIGHT = 0.4f;
    //Change this based on RPM Value given to shooter.java, this is just a baseline calculated from Collin's desmos
    public static final double BASE_RPM = 4 * 60 / 0.279d;
}
