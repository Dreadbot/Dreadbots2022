package frc.robot.command.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.subsystem.shooter.Shooter;

public class ShooterCommands {
    public static class Shoot extends SequentialCommandGroup {
        private final Shooter shooter;

        public Shoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                new ParallelCommandGroup(
                    new TurretCommands.ActiveTrack(shooter.getTurret()),
                    new HoodCommands.ActiveTrack(shooter.getHood()),
                    new FlywheelCommands.PrepareShot(shooter.getFlywheel())
                ),
                new FeedBallCommand(shooter)
            );
        }
    }

    public static class BlindShoot extends SequentialCommandGroup {
        private Shooter shooter;

        public BlindShoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                new FlywheelCommands.PrepareBlindShot(shooter.getFlywheel()),
                new FeedBallCommand(shooter)
            );
        }
    }
}
