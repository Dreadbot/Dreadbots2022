package frc.robot.command.shooter;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Flywheel;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.math.CargoKinematics;
import frc.robot.util.controls.VisionInterface;

public class FlywheelCommands {
    public static class PrepareVisionShot extends CommandBase {
        private final Flywheel flywheel;
        private final CargoKinematics cargoKinematics;

        private double commandedVelocity;

        public PrepareVisionShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = flywheel.getCargoKinematics();

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            commandedVelocity = velocity;
            SmartDashboard.putNumber("OUT VIP TEMP", commandedVelocity);
            flywheel.setVelocity(commandedVelocity);
        }

        @Override
        public boolean isFinished() {
            return flywheel.getTangentialVelocity() >= commandedVelocity;

//            return Math.abs(flywheel.getTangentialVelocity() - commandedVelocity) <= 0.15d;
        }
    }

    public static class EjectShootPreset extends CommandBase {
        private Shooter shooter;
        private double speedOnScore;
        private double speedOnEject;

        public EjectShootPreset(Shooter shooter, double speedOnScore, double speedOnEject) {
            this.shooter = shooter;

            this.speedOnScore = speedOnScore;
            this.speedOnEject = speedOnEject;

            addRequirements(shooter.getFlywheel());
        }

        @Override
        public void execute() {
            shooter.getFlywheel().setVelocity(getVelocity());
        }

        @Override
        public boolean isFinished() {
            return shooter.getFlywheel().isAtSetVelocity();
        }

        private double getVelocity() {
            if(!shooter.getColorSensor().isCorrectColor() &&
                shooter.getColorSensor().getBallColor() != null) return speedOnEject;

            return speedOnScore;
        }
    }

    public static class PreparePresetShot extends CommandBase {
        private Flywheel flywheel;
        private double velocity;

        public PreparePresetShot(Flywheel flywheel, double velocity) {
            this.flywheel = flywheel;
            this.velocity = velocity;

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            flywheel.setVelocity(velocity);
        }

        @Override
        public boolean isFinished() {
//            return Math.abs(flywheel.getTangentialVelocity() - velocity) <= 1.0d;

            return flywheel.isAtSetVelocity();
        }
    }
}
