package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.ColorSensor;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.VisionInterface;

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

    public static class EjectShoot extends SequentialCommandGroup {
        private final Shooter shooter;

        public EjectShoot(Shooter shooter) {
            this.shooter = shooter;

            addRequirements(shooter);
            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
//                    new TurretCommands.EjectTrack(shooter.getTurret()),
                    new HoodCommands.TurnToAngle(shooter.getHood(), 65.0)
                ),
                new FlywheelCommands.Spool(shooter.getFlywheel(), 1000.0),
                new FeedBallCommand(shooter)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFlywheel().idle();
        }
    }

    public static class ResetBallShot extends ParallelCommandGroup {
        public ResetBallShot(Shooter shooter) {
            addCommands(
                new FlywheelCommands.ReverseBall(shooter)
            );
        }
    }

    public static class PresetShoot extends SequentialCommandGroup {
        private Shooter shooter;

        private double afterAngle;

        public PresetShoot(Shooter shooter, double turretAngle, double hoodAngle, double flywheelSpeed, double afterAngle) {
            this.shooter = shooter;
            this.afterAngle = afterAngle;

            addRequirements(shooter);
            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.TurnToAngle(shooter.getTurret(), turretAngle),
                    new HoodCommands.TurnToAngle(shooter.getHood(), hoodAngle)
                ),
                new FlywheelCommands.Spool(shooter.getFlywheel(), flywheelSpeed),
                new FeedBallCommand(shooter)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFeeder().idle();
            shooter.getFlywheel().idle();
        }
    }

    public static class FeedBallCommand extends CommandBase {
        private Feeder feeder;
        private ColorSensor colorSensor;

        private double feedPosition = 0;

        public FeedBallCommand(Shooter shooter) {
            this.feeder = shooter.getFeeder();
            this.colorSensor = shooter.getColorSensor();

            addRequirements(colorSensor);
        }

        @Override
        public void initialize() {
            feedPosition = feeder.getFeederPosition();
        }

        @Override
        public void execute() {
            feeder.feed();
            SmartDashboard.putString("Ball fed", "Ball is being fed");
        }

        @Override
        public boolean isFinished() {
//            Color currentBallColor = colorSensor.getBallColor();
//            // Return true if different color ball, or if no ball is detected
//            return currentBallColor == null || currentBallColor != colorSensor.getInitialBallColor();
            return Math.abs(feeder.getFeederPosition() - feedPosition) > 100.0d;
        }
    }

    public static class LowShoot extends SequentialCommandGroup {
        private Shooter shooter;
        private Intake intake;

        public LowShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;
            this.intake = intake;

            addRequirements();
            addCommands(
                new ParallelRaceGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new IntakeCommand(intake)
                ),
                new ConditionalCommand(
                    new PresetShoot(shooter, 155.0, 65.0d, 1200.0d, 155.0d),
                    new PresetShoot(shooter, 65.0, 65.0d, 1600.0d, 155.0d),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
        }
    }

    public static class HighShoot extends ParallelCommandGroup {
        private Shooter shooter;

        public HighShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;

            addRequirements();
            addCommands(
                new IntakeCommand(intake),
                new ConditionalCommand(
                    // SHOOT
                    new ConditionalCommand(
                        new TargetShoot(shooter),
                        new PresetShoot(shooter, 155, 71.862, 3436.0d, 155.0d),
                        VisionInterface::canTrackHub
                    ),
                    // EJECT
                    new ConditionalCommand(
                        new EjectShoot(shooter),
                        new PresetShoot(shooter, 110, 71.862, 1500.0d, 155.0d),
                        VisionInterface::canTrackHub
                    ),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
        }
    }
}
