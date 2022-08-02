package frc.robot.command.shooter;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.ColorSensor;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.controls.VisionInterface;

public class ShooterCommands {
    public static class TargetShoot extends SequentialCommandGroup {
        private final Shooter shooter;

        public TargetShoot(Shooter shooter) {
            this.shooter = shooter;

            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.ActiveTrack(shooter.getTurret()),
                    new HoodCommands.ActiveTrack(shooter.getHood()),
                    new FlywheelCommands.PrepareVisionShot(shooter.getFlywheel())
                ),
                new FeedBallCommand(shooter),
                new WaitCommand(0.5)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFeeder().idle();
            shooter.getFlywheel().idle();
        }
    }

    public static class EjectShoot extends SequentialCommandGroup {
        private final Shooter shooter;

        public EjectShoot(Shooter shooter) {
            this.shooter = shooter;

            addCommands(
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.EjectTrack(shooter.getTurret()),
                    new HoodCommands.TurnToAngle(shooter.getHood(), 65.0)
                ),
                new FlywheelCommands.PreparePresetShot(shooter.getFlywheel(), 20.0),
                new FeedBallCommand(shooter)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFlywheel().idle();
            shooter.getFeeder().idle();
        }
    }

    public static class PresetShoot extends SequentialCommandGroup {
        private Shooter shooter;

        private double afterAngle;

        public PresetShoot(Shooter shooter, double turretAngle, double hoodAngle, double flywheelSpeed, double afterAngle) {
            this.shooter = shooter;
            this.afterAngle = afterAngle;

            addCommands(
                new InstantCommand(() -> System.out.println("PRESET SHOOT")),
                new ParallelCommandGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new TurretCommands.TurnToAngle(shooter.getTurret(), turretAngle),
                    new HoodCommands.TurnToAngle(shooter.getHood(), hoodAngle),
                    new FlywheelCommands.PreparePresetShot(shooter.getFlywheel(), flywheelSpeed)
                ),
                new FeedBallCommand(shooter),
                new WaitCommand(0.5)
            );
        }

        @Override
        public void end(boolean interrupted) {
            shooter.getFeeder().idle();
//            shooter.getFlywheel().idle();
        }
    }

    public static class FeedBallCommand extends CommandBase {
        private Feeder feeder;
        private Shooter shooter;
        private ColorSensor colorSensor;

        private Color colorFeeding;
        private double initialPosition;

        private boolean isBallSensed;
        private double targetDistance;

        public FeedBallCommand(Shooter shooter) {
            this.shooter = shooter;
            this.feeder = shooter.getFeeder();
            this.colorSensor = shooter.getColorSensor();

            addRequirements(colorSensor);
        }

        @Override
        public void initialize() {
            SmartDashboard.putString("CurrentLowShootCommand", "Feeding");

            colorFeeding = colorSensor.getBallColor();
            initialPosition = feeder.getFeederPosition();

            targetDistance = 100.0d;
        }

        @Override
        public void execute() {
            feeder.feed(0.6d);

            if(!isBallSensed && colorSensor.isBallDetected()) {
                isBallSensed = true;

                if(colorSensor.getBallColor() == colorFeeding) {
                    targetDistance += 120.0d;
                }
            }

            if(isBallSensed && !colorSensor.isBallDetected()) {
                isBallSensed = false;
            }
        }

        @Override
        public boolean isFinished() {
//            if(shooter.getColorSensor().isBallDetected() && !shooter.getColorSensor().isCorrectColor()) {
//                SmartDashboard.putString("STATUS", "OPPONENT BALL DETECTED");
////                feeder.idle();
//                return true;
//            }
//
//            if(shooter.getFlywheel().isBallImpulseDetected()) {
//                SmartDashboard.putString("STATUS", "IMPULSE");
//            } else {
//                SmartDashboard.putString("STATUS", "NONE");
//            }
//
//            return shooter.getFlywheel().isBallImpulseDetected();

            if(colorSensor.isBallDetected() && colorSensor.getBallColor() != colorFeeding) return true;
            if(Math.abs(feeder.getFeederPosition() - initialPosition) <= targetDistance) return false;

//            return !colorSensor.isBallDetected() || colorSensor.getBallColor() != colorFeeding;
            return false;
        }

        @Override
        public void end(boolean interrupted) {
            feeder.idle();
            targetDistance = 100.0d;
        }
    }

    public static class LowShoot extends SequentialCommandGroup {
        private Shooter shooter;
        private Intake intake;

        public LowShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;
            this.intake = intake;

            addCommands(
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kCoast)),
                new ParallelRaceGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new IntakeCommand(intake)
                ),
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kBrake)),
                new ConditionalCommand(
                    new PresetShoot(shooter, 149.0, 60.0d, 6d, 149.0d).raceWith(new IntakeCommand(intake, 1.0)),
                    new PresetShoot(shooter, 65.0, 60.0, 6d, 149.0d).raceWith(new IntakeCommand(intake, 1.0)),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
//            addCommands(
//                new IntakeCommand(intake),
//                new SequentialCommandGroup(
//                    new ParallelCommandGroup(
//                        new TurretCommands.EjectShootPreset(shooter, 149, 59),
//                        new HoodCommands.EjectShootPreset(shooter, 60, 60),
//                        new FlywheelCommands.EjectShootPreset(shooter, 6, 6)
//                    ),
//                    new FeedBallCommand(shooter)
//                )
//            );
        }
    }

    public static class TarmacPresetShoot extends SequentialCommandGroup {
        private Shooter shooter;
        private Intake intake;

        public TarmacPresetShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;
            this.intake = intake;

            addRequirements();
            addCommands(
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kCoast)),
                new ParallelRaceGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new IntakeCommand(intake)
                ),
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kBrake)),
                new ConditionalCommand(
                    new PresetShoot(shooter, 155.0, 67.710d,  7.539d, 155.0d).raceWith(new IntakeCommand(intake, 0.5)),
                    new PresetShoot(shooter, 65.0, 76.087d, 3.0d, 155.0d).raceWith(new IntakeCommand(intake, 0.5)),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
        }
    }

    public static class LongPresetShoot extends SequentialCommandGroup {
        private Shooter shooter;
        private Intake intake;

        public LongPresetShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;
            this.intake = intake;

            addRequirements();
            addCommands(
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kCoast)),
                new ParallelRaceGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new IntakeCommand(intake)
                ),
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kBrake)),
                new ConditionalCommand(
                    new PresetShoot(shooter, 155.0, 59.0609d, 7, 155.0d).raceWith(new IntakeCommand(intake, 0.5)),
                    new PresetShoot(shooter, 65.0, 76.087d, 12.0d, 155.0d).raceWith(new IntakeCommand(intake, 0.5)),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
        }
    }

    public static class HighShoot extends SequentialCommandGroup {
        private Shooter shooter;

        public HighShoot(Shooter shooter, Intake intake) {
            this.shooter = shooter;

            addRequirements();
            addCommands(
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kCoast)),
                new ParallelRaceGroup(
                    new WaitUntilCommand(shooter.getColorSensor()::isBallDetected),
                    new IntakeCommand(intake)
                ),
                new InstantCommand(() -> shooter.getFeeder().setIdleMode(CANSparkMax.IdleMode.kBrake)),
                new ConditionalCommand(
                    // SHOOT
                    new ConditionalCommand(
                        new TargetShoot(shooter),
                        new PresetShoot(shooter, 155.0, 76.087d, 7.0d, 155.0d),
                        VisionInterface::canTrackHub
                    ).alongWith(new IntakeCommand(intake)),
                    // EJECT
                    new ConditionalCommand(
//                        new EjectShoot(shooter),
                        new TargetShoot(shooter),
                        new PresetShoot(shooter, 110, 71.862, 7.0d, 155.0d),
                        VisionInterface::canTrackHub
                    ).alongWith(new IntakeCommand(intake)),
                    shooter.getColorSensor()::isCorrectColor
                )
            );
        }
    }
}
