package frc.robot.command.autonomous;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants;
import frc.robot.command.shooter.HoodCommands;
import frc.robot.command.shooter.ShooterCommands;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.DreadbotMecanumDrive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Hood;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.shooter.Turret;

public class FiveBallAuton extends SequentialCommandGroup {
    private PathPlannerTrajectory rich_first_leg = PathPlanner.loadPath("rich_first_leg", 5.0, 3.0);
    private PathPlannerTrajectory three_ball_second_leg = PathPlanner.loadPath("3ball_second_leg", 5.0, 3.0, true);
    private PathPlannerTrajectory three_ball_third_leg = PathPlanner.loadPath("3ball_third_leg", 5.0, 3.0);
    private PathPlannerTrajectory three_ball_fourth_leg = PathPlanner.loadPath("3ball_fourth_leg", 5.0, 3.0, true);

    public FiveBallAuton(Turret turret, Hood hood, DreadbotMecanumDrive drive, Intake intake, Shooter shooter) {
        addCommands(
            new ParallelCommandGroup(
                    new TurretCommands.Calibrate(turret, false)
                            .andThen(new TurretCommands.TurnToAngle(turret, 155.0d)),
                    new HoodCommands.Calibrate(hood, false)
                            .andThen(new HoodCommands.TurnToAngle(hood, Constants.MAX_HOOD_ANGLE)),
                    new TrajectoryAuton(
                            drive,
                            rich_first_leg,
                            8.0
                    ),
                    new InstantCommand(intake::intake, intake)
            ),
            new ShooterCommands.HighShoot(shooter, intake).raceWith(new WaitCommand(3.0)),
            new PrintCommand("SECOND PATH"),
            new TrajectoryAuton(
                    drive,
                    three_ball_second_leg,
                    8.0
            ),
            new ShooterCommands.HighShoot(shooter, intake).raceWith(new WaitCommand(1.5)),
            new PrintCommand("THIRD PATH"),
            new TrajectoryAuton(
                    drive,
                    three_ball_third_leg,
                    8.0
            ),
            new WaitCommand(1.0),
            new TrajectoryAuton(
                    drive,
                    three_ball_fourth_leg,
                    8.0
            ),
            new ShooterCommands.HighShoot(shooter, intake).raceWith(new WaitCommand(3.0)),
            new InstantCommand(intake::idle, intake)
        );
    }
}
