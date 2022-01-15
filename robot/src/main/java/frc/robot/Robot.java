// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  @SuppressWarnings("unused")
  private Joystick primaryJoystick;
  @SuppressWarnings("unused")
  private Joystick secondaryJoystick;

  @SuppressWarnings("unused")
  private CANSparkMax leftFrontDriveMotor;
  @SuppressWarnings("unused")
  private CANSparkMax rightFrontDriveMotor;
  @SuppressWarnings("unused")
  private CANSparkMax leftBackDriveMotor;
  @SuppressWarnings("unused")
  private CANSparkMax rightBackDriveMotor;
  
  @Override
  public void robotInit() {
    // Instantiate Joysticks
    primaryJoystick = new Joystick(Constants.PRIMARY_JOYSTICK_PORT);
    secondaryJoystick = new Joystick(Constants.SECONDARY_JOYSTICK_PORT);

    // Instantiate Drivetrain motors
    leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
