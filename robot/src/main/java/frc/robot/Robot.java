// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.Console;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.DreadbotController;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
  @SuppressWarnings("unused")
  private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

  private CANSparkMax leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
  private CANSparkMax rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
  private CANSparkMax leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
  private CANSparkMax rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);

  private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

  private CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);
  private Intake intake = new Intake(intakeMotor);

  private final CANSparkMax flywheelMotor = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax hoodMotor = new CANSparkMax(2, MotorType.kBrushless);
  private final CANSparkMax turretMotor = new CANSparkMax(1, MotorType.kBrushless);
  @SuppressWarnings("unused")
  private final DigitalInput leftSwitchDigitalInput = new DigitalInput(1); // add to constants later
  private final DigitalInput rightSwitchDigitalInput = new DigitalInput(2);
  @SuppressWarnings("unused")
  private final Turret turret = new Turret(leftSwitchDigitalInput, rightSwitchDigitalInput, turretMotor); // temporary for board

  private Shooter shooter = new Shooter(flywheelMotor, hoodMotor, turretMotor);

  private final Solenoid leftNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
  //private final Solenoid rightNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.RIGHT_NEUTRAL_HOOK_ACTUATOR);
  private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 1);

  private final CANSparkMax winchMotor = new CANSparkMax(Constants.WINCH_MOTOR_PORT, MotorType.kBrushless);
  @SuppressWarnings("unused")
  private Climber climber = new Climber(leftNeutralHookActuator/*, rightNeutralHookActuator*/, climbingHookActuator, winchMotor);
  
  @Override
  public void robotInit() {
    if(!Constants.DRIVE_ENABLED) {
      leftFrontDriveMotor.close();
      rightFrontDriveMotor.close();
      leftBackDriveMotor.close();
      rightBackDriveMotor.close();
    }

    if(!Constants.CLIMB_ENABLED) {
      leftNeutralHookActuator.close();
      // rightNeutralHookActuator.close();

      winchMotor.close();
    }
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
   
    if(primaryController.isRightTriggerPressed()) {
      climber.extendArm();
    }
    if(primaryController.isRightBumperPressed()) {
      climber.halfExtendArm();
    }
    if(primaryController.isLeftTriggerPressed()){
      climber.retractArm();
    }
    //drive.driveCartesian(primaryController.getYAxis(), primaryController.getXAxis(), 0);
    turret.switchDebug();
    drive.driveCartesian(primaryController.getYAxis(), primaryController.getXAxis(), 0);
    turret.calibrateTurret();

    if(secondaryController.isBButtonPressed())
      shooter.shoot();
    else 
      shooter.idle();
    if(secondaryController.isAButtonPressed()) 
      intake.intake();
    if(secondaryController.isXButtonPressed()) 
      intake.outlet();
    if(secondaryController.isAButtonPressed() == secondaryController.isXButtonPressed()) 
      intake.idle();
    if(primaryController.isAButtonPressed())
      climber.rotateNeutralHooksVertical();
    if(primaryController.isBButtonPressed())
      climber.rotateNeutralHooksDown();
    if(primaryController.isXButtonPressed())
      climber.rotateClimbingHookVertical();
    if(primaryController.isYButtonPressed())
      climber.rotateClimbingHookDown();
  }
  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}
}
