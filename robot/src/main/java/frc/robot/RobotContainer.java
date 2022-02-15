package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.command.climber.RotateClimbingArmVerticalCommand;
import frc.robot.command.climber.AutonomousClimberCommand;
import frc.robot.command.climber.ExtendArmCommand;
import frc.robot.command.climber.RetractArmCommand;
import frc.robot.command.climber.RotateClimbingArmDownCommand;
import frc.robot.command.climber.RotateNeutralHookDownCommand;
import frc.robot.command.climber.RotateNeutralHookVerticalCommand;
import frc.robot.command.drive.DriveCommand;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.command.intake.OuttakeCommand;
import frc.robot.command.shooter.HoodCalibrationCommand;
import frc.robot.command.shooter.ShootCommand;
import frc.robot.command.shooter.TurretCalibrationCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Flywheel;
import frc.robot.subsystem.shooter.Hood;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private CANSparkMax leftFrontDriveMotor = new CANSparkMax(1, MotorType.kBrushless);
    private CANSparkMax rightFrontDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
    private CANSparkMax leftBackDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
    private CANSparkMax rightBackDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
    private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private CANSparkMax intakeMotor = new CANSparkMax(5, MotorType.kBrushless);
    private Intake intake = new Intake(intakeMotor);

    private final CANSparkMax feederMotor = new CANSparkMax(6, MotorType.kBrushless);
    private final Feeder feeder = new Feeder(feederMotor);

    private final CANSparkMax flywheelMotor = new CANSparkMax(8, MotorType.kBrushless);
    private final Flywheel flywheel = new Flywheel(flywheelMotor);

    private final CANSparkMax hoodMotor = new CANSparkMax(9, MotorType.kBrushless);
    private final DigitalInput lowerHoodLimitSwitch = new DigitalInput(2);
    private final DigitalInput upperHoodLimitSwitch = new DigitalInput(3);
    private final Hood hood = new Hood(hoodMotor, lowerHoodLimitSwitch, upperHoodLimitSwitch);

    private final CANSparkMax turretMotor = new CANSparkMax(7, MotorType.kBrushless);
    private final DigitalInput lowerTurretLimitSwitch = new DigitalInput(0); //pls fix this
    private final DigitalInput upperTurretLimitSwitch = new DigitalInput(1);
    private final Turret turret = new Turret(turretMotor, lowerTurretLimitSwitch, upperTurretLimitSwitch);
    
    private Shooter shooter = new Shooter(feeder, flywheel, hood, turret);

    private final Solenoid neutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 0);

    private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
    private final CANSparkMax winchMotor = new CANSparkMax(1, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(5);
    private final DigitalInput topLimitSwitch = new DigitalInput(4);
    private Climber climber = new Climber(neutralHookActuator, climbingHookActuator, winchMotor, bottomLimitSwitch, topLimitSwitch);
    
    public RobotContainer() {   
        drive.setDefaultCommand(new DriveCommand(drive,
            primaryController::getYAxis,
            primaryController::getXAxis,
            primaryController::getZAxis));
        
        intake.setDefaultCommand(new RunCommand(intake::idle, intake));

        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));
        
        feeder.setDefaultCommand(new RunCommand(feeder::idle, feeder));
        flywheel.setDefaultCommand(new RunCommand(flywheel::idle, flywheel)
            .andThen(new InstantCommand(() -> SmartDashboard.putNumber("Flywheel Velocity (RPM)", flywheel.getVelocity()))));
        
        SmartDashboard.putNumber("Selected Turret Angle", 150);
        turret.setDefaultCommand(new RunCommand(() -> turret.setAngle(SmartDashboard.getNumber("Selected Turret Angle", 150)), turret));

        SmartDashboard.putNumber("Selected Hood Angle", 68);
        hood.setDefaultCommand(new RunCommand(() -> hood.setAngle(SmartDashboard.getNumber("Selected Hood Angle", 68)), hood));
        
        secondaryController.getBButton().whileActiveOnce(new ShootCommand(shooter));
        secondaryController.getYButton().whileHeld(new InstantCommand(shooter::feedBall, feeder));

        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
        primaryController.getAButton().whenPressed(new RotateNeutralHookVerticalCommand(climber));
        primaryController.getBButton().whenPressed(new RotateNeutralHookDownCommand(climber));
        primaryController.getXButton().whenPressed(new RotateClimbingArmVerticalCommand(climber));
        primaryController.getYButton().whenPressed(new RotateClimbingArmDownCommand(climber));
        primaryController.getRightTrigger().whenPressed(new ExtendArmCommand(climber));
        primaryController.getLeftTrigger().whenPressed(new RetractArmCommand(climber));
        primaryController.getRightBumper().whenPressed(new AutonomousClimberCommand(climber));

        feeder.setDefaultCommand(new RunCommand(feeder::idle, feeder));
        secondaryController.getBButton().whileHeld(new InstantCommand(feeder::feed, feeder));

        drive.setDefaultCommand(new DriveCommand(drive, 
            primaryController::getYAxis, 
            primaryController::getXAxis,
            primaryController::getZAxis));
    }

    public void periodic() {
    }

    public void calibrate() {
        CommandScheduler.getInstance().schedule(new TurretCalibrationCommand(turret));
        CommandScheduler.getInstance().schedule(new HoodCalibrationCommand(hood));
    }
}
