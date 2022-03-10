package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystem.shooter.ColorSensor;
import frc.robot.subsystem.shooter.Shooter;

public class ShooterCommands {
    public static class TargetShoot extends SequentialCommandGroup {
        private final Shooter shooter;

        public TargetShoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.ActiveTrack(shooter.getTurret()),
                    new HoodCommands.ActiveTrack(shooter.getHood())
                ),
                new FlywheelCommands.PrepareShot(shooter.getFlywheel()),
                new FeedBallCommand(shooter)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFlywheel().idle();
        }
    }

    public static class PresetShoot extends SequentialCommandGroup {
        private Shooter shooter;

        public PresetShoot(Shooter shooter, double turretAngle, double hoodAngle) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.TurnToAngle(shooter.getTurret(), turretAngle),
                    new HoodCommands.TurnToAngle(shooter.getHood(), hoodAngle)
                ),
                new FlywheelCommands.PrepareBlindShot(shooter.getFlywheel()),
                new FeedBallCommand(shooter),
                new WaitCommand(1.0)
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

    public static class LowShoot extends SequentialCommandGroup {
        private Shooter shooter;

        public LowShoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new PresetShoot(shooter, 155.0, 70.0d)
            );
        }
    }

    public static class HighShoot extends SequentialCommandGroup {
        private Shooter shooter;

        public HighShoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new TargetShoot(shooter)
            );
        }
    }
}
