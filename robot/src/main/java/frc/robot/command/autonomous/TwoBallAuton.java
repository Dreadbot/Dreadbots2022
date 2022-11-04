package frc.robot.command.autonomous;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.command.shooter.HoodCommands;
import frc.robot.command.shooter.ShooterCommands;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.DreadbotMecanumDrive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Hood;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.shooter.Turret;

public class TwoBallAuton extends SequentialCommandGroup {
    private PathPlannerTrajectory examplePath = PathPlanner.loadPath("scarce_first_leg", 5.0, 3.0);

    public TwoBallAuton(Turret turret, Hood hood, DreadbotMecanumDrive drive, Intake intake, Shooter shooter) {
        addCommands(
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
            new WaitCommand(1.0),
            new ShooterCommands.HighShoot(shooter, intake).raceWith(new WaitCommand(3.0)),
            new InstantCommand(intake::idle, intake)
        );
    }
}
