package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.command.autonomous.FiveBallAuton;
import frc.robot.command.autonomous.ThreeBallAuton;
import frc.robot.command.autonomous.TwoBallAuton;
import frc.robot.command.climber.*;
import frc.robot.command.drive.DriveCommand;
import frc.robot.command.drive.TurboCommand;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.command.intake.OuttakeCommand;
import frc.robot.command.shooter.HoodCommands;
import frc.robot.command.shooter.ShooterCommands;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DreadbotMecanumDrive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.*;
import frc.robot.util.ClimbLevel;
import frc.robot.util.controls.DreadbotController;
import frc.robot.util.controls.VisionInterface;

public class RobotContainer {
    private final DreadbotController primaryController;
    private final DreadbotController secondaryController;
    private final DreadbotMecanumDrive drive;
    private final Intake intake;
    private final Feeder feeder;
    private final Climber climber;
    private final Turret turret;
    private final Flywheel flywheel;
    private final Hood hood;
    private final Shooter shooter;

    private Color teamColor;
    SendableChooser<Color> teamColorChooser;

    SendableChooser<Integer> autonChooser;

    public RobotContainer() {
        primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
        secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

        // Set the default camera view
        VisionInterface.selectCamera(2);

        // Set default values for Flywheel speed & angle (used for tuning/test shots)
        SmartDashboard.putNumber("Flywheel velocity &", 10d);
        SmartDashboard.putNumber("Flywheel hood angle", 60.0d);

        teamColorChooser = new SendableChooser<>();
        teamColorChooser.setDefaultOption("Blue Alliance", Constants.COLOR_BLUE);
        teamColorChooser.addOption("Red Alliance", Constants.COLOR_RED);
        SmartDashboard.putData(teamColorChooser);
        setTeamColor();

        autonChooser = new SendableChooser<>();
        autonChooser.setDefaultOption("2-ball", 2);
        autonChooser.addOption("3-ball", 3);
        autonChooser.addOption("5-ball", 5);
        SmartDashboard.putData(autonChooser);

        drive = new DreadbotMecanumDrive();
        intake = new Intake();
        feeder = new Feeder();
        turret = new Turret();
        flywheel = new Flywheel();
        hood = new Hood();
        shooter = new Shooter(feeder, flywheel, hood, turret, new ColorSensor());
        climber = new Climber();

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        DriveCommand driveCommand = new DriveCommand(drive,
                primaryController::getYAxis,
                primaryController::getXAxis,
                primaryController::getZAxis
        );
        // Drive Commands
        drive.setDefaultCommand(driveCommand);

        // Intake Commands
        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake, feeder, flywheel));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));

        // Shooter Commands
        primaryController.getRightBumper().whenHeld(new TurboCommand(driveCommand));
        hood.setDefaultCommand(new HoodCommands.PassiveTrack(hood));
        turret.setDefaultCommand(new TurretCommands.PassiveTrack(turret, drive));
        //Speed up turret later?
        flywheel.setDefaultCommand(new RunCommand(flywheel::idle, flywheel));
        secondaryController.getBButton().whileHeld(new ShooterCommands.LowShoot(shooter, intake));
        secondaryController.getYButton().whileHeld(new ShooterCommands.HighShoot(shooter, intake));
        secondaryController.getStartButton().whileHeld(new ShooterCommands.TarmacPresetShoot(shooter, intake));
        secondaryController.getBackButton().whileHeld(new ShooterCommands.LongPresetShoot(shooter, intake));

        // Climber Commands
        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
        primaryController.getYButton().whenPressed(new RotateNeutralHookVerticalCommand(climber));
        primaryController.getXButton().whenPressed(new RotateNeutralHookDownCommand(climber));
        primaryController.getBButton().whenPressed(new RotateClimbingArmVerticalCommand(climber, drive));
        primaryController.getAButton().whenPressed(new RotateClimbingArmDownCommand(climber));
        primaryController.getRightTrigger().whenPressed(new ExtendArmCommand(climber, drive, ClimbLevel.MEDIUM));
        primaryController.getLeftTrigger().whenPressed(new RetractArmCommand(climber));

        primaryController.getLeftBumper().whenPressed(new MediumClimb(climber, drive, turret));
        primaryController.getBackButton().whenPressed(new HighClimb(climber, drive, turret));
        primaryController.getStartButton().whenPressed(new TraverseClimb(climber, drive, turret));
    }

    public Command getAutonomousCommand() {
        if (autonChooser.getSelected() == 2)
            return get2BallAuton();
        if (autonChooser.getSelected() == 3)
            return get3BallAuton();
        if (autonChooser.getSelected() == 5)
            return get5BallAuton();
        return new InstantCommand(intake::idle, intake);
    }

    /**
     * Command flow:
     *      * Calibrate Turret and Hood, and at the same time, turn on the intake and start moving the initial leg of
     *      movement - wait for all these to be done (calibrated, moved to new location, intake is on)
     *      * Wait 1 second to make sure everything is stable
     *      * Shoot all balls in the robot (should be 2: 1 from initial load, and 1 picked up while moving in step 1)
     *      * Stop intake, wait for telliop mode to start
     */
    public Command get2BallAuton() {
        return new TwoBallAuton(turret, hood, drive, intake, shooter);
    }

    public Command get3BallAuton() {
        return new ThreeBallAuton(turret, hood, drive, intake, shooter);
    }

    public Command get5BallAuton() {
        return new FiveBallAuton(turret, hood, drive, intake, shooter);
    }

    /**
     * Initial calibration of climber and shooter (turret and hood)
     * Ensure climb hooks are in correct position
     * Retract climb to fully down position - record min rotations, compute max extension by adding range constant
     *      to min
     * Rotate Turret to lower limit switch - record min rotations, compute max extension by adding range constant
     *      to min - can be configured to do "full" calibration where it rotates to lower and then upper limit switch
     *      instead of using the hard-coded range constant
     * Rotate Hood to lower limit switch - record min rotations, compute max extension by adding range constant
     *      to min - can be configured to do "full" calibration where it rotates to lower and then upper limit switch
     *      instead of using the hard-coded range constant
     */
    public void calibrate() {
        CommandScheduler.getInstance().schedule(
                false,
                new SequentialCommandGroup(
                        new RotateNeutralHookDownCommand(climber),
                        new RotateClimbingArmDownCommand(climber),
                        new RetractArmCommand(climber),
                        new InstantCommand(climber::updateRetractedPosition)
                )
        );

        CommandScheduler.getInstance().schedule(false, new TurretCommands.Calibrate(turret, false)
                .andThen(new TurretCommands.TurnToAngle(turret, 149.0d)));

        CommandScheduler.getInstance().schedule(false, new HoodCommands.Calibrate(hood, false)
                .andThen(new HoodCommands.TurnToAngle(hood, Constants.MAX_HOOD_ANGLE)));
    }

    /**
     * Grab Alliance Color from the smart dashboard
     * This will be used to determine if a ball is the right color or not
     * and either shoot it into the goal or off to the side
     */
    public void setTeamColor() {
        teamColor = teamColorChooser.getSelected();
        if (teamColor == Constants.COLOR_BLUE)
            SmartDashboard.putString("Team color 2", "Blue");
        else if (teamColor == Constants.COLOR_RED)
            SmartDashboard.putString("Team color 2", "Red");
    }
}