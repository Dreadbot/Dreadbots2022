package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.command.autonomous.TrajectoryAuton;
import frc.robot.command.climber.*;
import frc.robot.command.drive.DriveCommand;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.command.intake.OuttakeCommand;
import frc.robot.command.shooter.HoodCommands;
import frc.robot.command.shooter.ShooterCommands;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.*;
import frc.robot.util.controls.DreadbotController;
import frc.robot.util.controls.VisionInterface;
import frc.robot.util.DreadbotMotor;

public class RobotContainer {
    private final DreadbotController primaryController;
    private final DreadbotController secondaryController;

    private final ColorSensor dreadbotColorSensor;
    private final Drive drive;
    private final Intake intake;
    private final Feeder feeder;
    private final Climber climber;
    private final Turret turret;
    private final Flywheel flywheel;
    private final Hood hood;
    private final Shooter shooter;    

    private Color teamColor;
    SendableChooser<Color> teamColorChooser;
    
    public RobotContainer() {
        VisionInterface.selectCamera(2);

        primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
        secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

        teamColorChooser = new SendableChooser<>();
        teamColorChooser.setDefaultOption("Blue Alliance", Constants.COLOR_BLUE);
        teamColorChooser.addOption("Red Alliance", Constants.COLOR_RED);
        SmartDashboard.putData(teamColorChooser);
        setTeamColor();

        if (Constants.DRIVE_ENABLED) {
            DreadbotMotor leftFrontDriveMotor = new DreadbotMotor(new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Front Left Drive");
            DreadbotMotor rightFrontDriveMotor = new DreadbotMotor(new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Front Right Drive");
            DreadbotMotor leftBackDriveMotor = new DreadbotMotor (new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Back Left Drive");
            DreadbotMotor rightBackDriveMotor = new DreadbotMotor (new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Back Right Drive");

            AHRS gyroscope = new AHRS(Constants.GYROSCOPE_PORT);

            drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor, gyroscope);
        } else drive = new Drive();

        if (Constants.INTAKE_ENABLED) {
            DreadbotMotor intakeMotor = new DreadbotMotor (new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless), "Intake");

            intake = new Intake(intakeMotor);
        } else intake = new Intake();

        if (Constants.FEEDER_ENABLED) {
            DreadbotMotor feederMotor = new DreadbotMotor(new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless), "Feeder");

            feeder = new Feeder(feederMotor);
        } else feeder = new Feeder();

        if (Constants.TURRET_ENABLED) {
            DreadbotMotor turretMotor = new DreadbotMotor(new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless), "Turret");
            DigitalInput lowerTurretLimitSwitch = new DigitalInput(Constants.LOWER_TURRET_LIMIT_SWITCH_ID);
            DigitalInput upperTurretLimitSwitch = new DigitalInput(Constants.UPPER_TURRET_LIMIT_SWITCH_ID);

            turret = new Turret(turretMotor, lowerTurretLimitSwitch, upperTurretLimitSwitch);
        } else turret = new Turret();

        if (Constants.FLYWHEEL_ENABLED) {
            DreadbotMotor flywheelMotor = new DreadbotMotor(new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless), "Flywheel");

            flywheel = new Flywheel(flywheelMotor);
        } else flywheel = new Flywheel();

        if (Constants.HOOD_ENABLED) {
            DreadbotMotor hoodMotor = new DreadbotMotor(new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless), "Hood");
            DigitalInput lowerHoodLimitSwitch = new DigitalInput(Constants.LOWER_HOOD_LIMIT_SWITCH_ID);
            DigitalInput upperHoodLimitSwitch = new DigitalInput(Constants.UPPER_HOOD_LIMIT_SWITCH_ID);

            hood = new Hood(hoodMotor, lowerHoodLimitSwitch, upperHoodLimitSwitch);
        } else hood = new Hood();

        if (Constants.COLOR_SENSOR_ENABLED) {
            ColorSensorV3 colorSensorV3 = new ColorSensorV3(Constants.I2C_PORT);
            dreadbotColorSensor = new ColorSensor(colorSensorV3);
        } else dreadbotColorSensor = new ColorSensor();

        if (Constants.SHOOTER_ENABLED) {
            shooter = new Shooter(feeder, flywheel, hood, turret, dreadbotColorSensor);
        } else shooter = new Shooter();

        if (Constants.CLIMB_ENABLED) {
            PneumaticHub pneumaticHub = new PneumaticHub(21);
            Solenoid neutralHookActuator = pneumaticHub.makeSolenoid(Constants.NEUTRAL_HOOK_SOLENOID_ID);
            Solenoid climbingHookActuator = pneumaticHub.makeSolenoid(Constants.POWER_HOOK_SOLENOID_ID);
            DreadbotMotor winchMotor = new DreadbotMotor(new CANSparkMax(Constants.WINCH_MOTOR_PORT, MotorType.kBrushless), "Winch");
            DigitalInput bottomLimitSwitch = new DigitalInput(Constants.CLIMBER_LIMIT_SWITCH_ID);

            climber = new Climber(neutralHookActuator, climbingHookActuator, winchMotor, bottomLimitSwitch);
        } else climber = new Climber();

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Drive Commands
        drive.setDefaultCommand(new DriveCommand(drive,
            primaryController::getYAxis,
            primaryController::getXAxis,
            primaryController::getZAxis));

        // Intake Commands
        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake, feeder));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));

        // Shooter Commands
        hood.setDefaultCommand(new HoodCommands.PassiveTrack(hood));
        turret.setDefaultCommand(new TurretCommands.PassiveTrack(turret, drive));
        flywheel.setDefaultCommand(new RunCommand(flywheel::idle, flywheel));
        secondaryController.getBButton().whileHeld(new ShooterCommands.LowShoot(shooter, intake));
        secondaryController.getYButton().whileHeld(new ShooterCommands.HighShoot(shooter, intake));
        secondaryController.getStartButton().whileHeld(new ShooterCommands.TarmacPresetShoot(shooter, intake));
        secondaryController.getBackButton().whileHeld(new ShooterCommands.LongPresetShoot(shooter, intake));

        // Climber Commands
        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
        primaryController.getYButton().whenPressed(new RotateNeutralHookVerticalCommand(climber));
        primaryController.getXButton().whenPressed(new RotateNeutralHookDownCommand(climber));
        primaryController.getBButton().whenPressed(new RotateClimbingArmVerticalCommand(climber));
        primaryController.getAButton().whenPressed(new RotateClimbingArmDownCommand(climber));
        primaryController.getRightTrigger().whenPressed(new ExtendArmCommand(climber));
        primaryController.getLeftTrigger().whenPressed(new RetractArmCommand(climber));
        primaryController.getStartButton().whenPressed(new MediumClimb(climber, turret));
    }

    public Command getAutonomousCommand(){
        PathPlannerTrajectory examplePath = PathPlanner.loadPath("scarce_first_leg", 5.0, 3.00);

        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                new TurretCommands.Calibrate(turret, false)
                    .andThen(new TurretCommands.TurnToAngle(turret, 155.0d)),
                new HoodCommands.Calibrate(hood, false)
                    .andThen(new HoodCommands.TurnToAngle(hood, Constants.MAX_HOOD_ANGLE)),
                new TrajectoryAuton(
                    drive,
                    examplePath,
                    8.0
                ),
                new InstantCommand(intake::intake, intake)
            ),
            new ShooterCommands.HighShoot(shooter, intake),
            new WaitCommand(2.0),
            new ShooterCommands.HighShoot(shooter, intake),
            new InstantCommand(intake::idle, intake)
        );
    }

    public void calibrate() {
        CommandScheduler.getInstance().schedule(false, new TurretCommands.Calibrate(turret, false)
            .andThen(new TurretCommands.TurnToAngle(turret, 149.0d)));

        CommandScheduler.getInstance().schedule(false, new HoodCommands.Calibrate(hood, false)
            .andThen(new HoodCommands.TurnToAngle(hood, Constants.MAX_HOOD_ANGLE)));

        CommandScheduler.getInstance().schedule(false, new RetractArmCommand(climber));
    }

    /*
     * Grab Alliance Color from the smart dashboard
     * This will be used to determine if a ball is the right color or not
     * and either shoot it into the goal or off to the side
     */
    public void setTeamColor(){
        teamColor = teamColorChooser.getSelected();
        if(teamColor == Constants.COLOR_BLUE)
            SmartDashboard.putString("Team color 2", "Blue");
        else if (teamColor == Constants.COLOR_RED)
            SmartDashboard.putString("Team color 2", "Red");
    }
}