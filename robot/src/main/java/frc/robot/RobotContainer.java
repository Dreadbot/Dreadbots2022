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
    private final DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    private final DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private final CANSparkMax leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private final Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private final CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);
    private final Intake intake = new Intake(intakeMotor);

    private final CANSparkMax feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless);
    private final Feeder feeder = new Feeder(feederMotor);

    private final CANSparkMax turretMotor = new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless);
    private final DigitalInput lowerTurretLimitSwitch = new DigitalInput(Constants.LOWER_TURRET_LIMIT_SWITCH_ID);
    private final DigitalInput upperTurretLimitSwitch = new DigitalInput(Constants.UPPER_TURRET_LIMIT_SWITCH_ID);
    private final Turret turret = new Turret(turretMotor, lowerTurretLimitSwitch, upperTurretLimitSwitch);

    private final CANSparkMax flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
    private final Flywheel flywheel = new Flywheel(flywheelMotor);

    private final CANSparkMax hoodMotor = new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless);
    private final DigitalInput lowerHoodLimitSwitch = new DigitalInput(Constants.LOWER_HOOD_LIMIT_SWITCH_ID);
    private final DigitalInput upperHoodLimitSwitch = new DigitalInput(Constants.UPPER_HOOD_LIMIT_SWITCH_ID);
    private final Hood hood = new Hood(hoodMotor, lowerHoodLimitSwitch, upperHoodLimitSwitch);

    private final Shooter shooter = new Shooter(feeder, flywheel, hood, turret);

    private final Solenoid neutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.NEUTRAL_HOOK_ACTUATOR_ID);
    private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.CLIMBING_HOOK_ACTUATOR_ID);
    private final CANSparkMax winchMotor = new CANSparkMax(Constants.WINCH_MOTOR_PORT, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(Constants.BOTTOM_CLIMBER_LIMIT_SWITCH_ID);
    private final DigitalInput topLimitSwitch = new DigitalInput(Constants.TOP_CLIMBER_LIMIT_SWITCH_ID);
    private final Climber climber = new Climber(neutralHookActuator, climbingHookActuator, winchMotor, bottomLimitSwitch, topLimitSwitch);
    
    public RobotContainer() {
        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Drive Commands
        drive.setDefaultCommand(new DriveCommand(drive,
            primaryController::getYAxis,
            primaryController::getXAxis,
            primaryController::getZAxis));

        // Intake Commands
        intake.setDefaultCommand(new RunCommand(intake::idle, intake));
        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));

        // Feeder Commands
        feeder.setDefaultCommand(new RunCommand(feeder::idle, feeder));

        // Flywheel Commands
        flywheel.setDefaultCommand(new RunCommand(flywheel::idle, flywheel)
            .andThen(new InstantCommand(() -> SmartDashboard.putNumber("Flywheel Velocity (RPM)", flywheel.getVelocity()))));

        // Hood Commands
        SmartDashboard.putNumber("Selected Hood Angle", 68);
        hood.setDefaultCommand(new RunCommand(() -> hood.setAngle(SmartDashboard.getNumber("Selected Hood Angle", 68)), hood));

        // Turret Commands
        SmartDashboard.putNumber("Selected Turret Angle", 150);
        turret.setDefaultCommand(new RunCommand(() -> turret.setAngle(SmartDashboard.getNumber("Selected Turret Angle", 150)), turret));

        // Shooter Commands
        secondaryController.getBButton().whileActiveOnce(new ShootCommand(shooter));
        secondaryController.getYButton().whileHeld(new InstantCommand(shooter::feedBall, feeder));

        // Climber Commands
        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
        primaryController.getAButton().whenPressed(new RotateNeutralHookVerticalCommand(climber));
        primaryController.getBButton().whenPressed(new RotateNeutralHookDownCommand(climber));
        primaryController.getXButton().whenPressed(new RotateClimbingArmVerticalCommand(climber));
        primaryController.getYButton().whenPressed(new RotateClimbingArmDownCommand(climber));
        primaryController.getRightTrigger().whenPressed(new ExtendArmCommand(climber));
        primaryController.getLeftTrigger().whenPressed(new RetractArmCommand(climber));
        primaryController.getRightBumper().whenPressed(new AutonomousClimberCommand(climber));
    }

    public void calibrate() {
        CommandScheduler.getInstance().schedule(new TurretCalibrationCommand(turret));
        CommandScheduler.getInstance().schedule(new HoodCalibrationCommand(hood));
    }
}
