package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystem.shooter.ColorSensor;
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

        @Override
        public void end(boolean interrupted) {
            shooter.getFlywheel().idle();
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

        @Override
        public void end(boolean interrupted) {
            shooter.getFlywheel().idle();
        }
    }

    public static class FeedBallCommand extends CommandBase {
        private Shooter shooter;
        private ColorSensor colorSensor;

        public FeedBallCommand(Shooter shooter) {
            this.shooter = shooter;
            this.colorSensor = shooter.getColorSensor();

            addRequirements(colorSensor);
        }

        @Override
        public void execute() {
            shooter.feedBall();
            SmartDashboard.putString("Ball fed", "Ball is being fed");
        }

        @Override
        public boolean isFinished() {
            Color currentBallColor = colorSensor.getBallColor();
            // Return true if different color ball, or if no ball is detected
            return currentBallColor == null || currentBallColor != colorSensor.getInitialBallColor();
        }
    }
}
