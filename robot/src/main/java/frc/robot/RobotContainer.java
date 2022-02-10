package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.command.climber.RotateClimbingHookVerticalCommand;
import frc.robot.command.climber.ExtendArmCommand;
import frc.robot.command.climber.RetractArmCommand;
import frc.robot.command.climber.RotateClimbingHookDownCommand;
import frc.robot.command.climber.RotateNeutralHookDownCommand;
import frc.robot.command.climber.RotateNeutralHookVerticalCommand;
import frc.robot.command.drive.DriveCommand;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.command.intake.OuttakeCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.Climber.ClimberState;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private CANSparkMax leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private CANSparkMax intakeMotor = new CANSparkMax(5, MotorType.kBrushless);
    private Intake intake = new Intake(intakeMotor);

    private final CANSparkMax flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax hoodMotor = new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax turretMotor = new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless);
    private Shooter shooter = new Shooter(flywheelMotor, hoodMotor, turretMotor);

    private final Solenoid neutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
    private final CANSparkMax feederMotor = new CANSparkMax(6, MotorType.kBrushless);
    private final Feeder feeder = new Feeder(feederMotor);

    private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
    private final CANSparkMax winchMotor = new CANSparkMax(1, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(1);
    private final DigitalInput topLimitSwitch = new DigitalInput(0);
    private Climber climber = new Climber(neutralHookActuator, climbingHookActuator, winchMotor, bottomLimitSwitch, topLimitSwitch);
    
    public RobotContainer() {    
        intake.setDefaultCommand(new RunCommand(intake::idle, intake));

        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
        primaryController.getAButton().whenPressed(new RotateNeutralHookVerticalCommand(climber));
        primaryController.getBButton().whenPressed(new RotateNeutralHookDownCommand(climber));
        primaryController.getXButton().whenPressed(new RotateClimbingHookVerticalCommand(climber));
        primaryController.getYButton().whenPressed(new RotateClimbingHookDownCommand(climber));
        primaryController.getRightTrigger().whenPressed(new ExtendArmCommand(climber));
        primaryController.getLeftTrigger().whenPressed(new RetractArmCommand(climber));

        feeder.setDefaultCommand(new RunCommand(feeder::idle, feeder));
        secondaryController.getBButton().whileHeld(new InstantCommand(feeder::feed, feeder));

        drive.setDefaultCommand(new DriveCommand(drive, 
            primaryController::getYAxis, 
            primaryController::getXAxis,
            primaryController::getZAxis));
    }

    public void periodic() {
        if(secondaryController.isBButtonPressed())
            shooter.shoot();
        else 
            shooter.idle();
        
        if(primaryController.isRightTriggerPressed()) {
            climber.extendArm();
        }
        if(primaryController.isRightBumperPressed()) {
            climber.halfExtendArm();
        }
        if(primaryController.isLeftTriggerPressed()){
            climber.retractArm();
        }
    }
}
