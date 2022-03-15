package frc.robot.command.shooter;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Flywheel;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.CargoKinematics;
import frc.robot.util.VisionInterface;
import frc.robot.util.tuning.SparkMaxTuningUtility;

public class FlywheelCommands {
    public static final double LOB_RPM = 200.0d;
    public static final double TANGENTIAL_VELOCITY_CONVERSION = 215.05d;
    static {
        SmartDashboard.putNumber("DEBUG RPM CONV", 1.0d);
    }

    public static class PrepareShot extends CommandBase {
        private final Flywheel flywheel;
        private final CargoKinematics cargoKinematics;

        private double lastDistanceToHub;
        private double currentCommandedRPM;

        public PrepareShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = new CargoKinematics(s -> 0.306*s + 2.6, 0.5715, 2.6416);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            SmartDashboard.putNumber("COMMANDED RPM", currentCommandedRPM);
            if(distanceToHub != lastDistanceToHub) velocityControl(velocity);
            lastDistanceToHub = distanceToHub;
        }

        @Override
        public boolean isFinished() {
            return flywheel.isAtSetVelocity();
        }

        private void velocityControl(double velocity) {
            currentCommandedRPM = velocity * TANGENTIAL_VELOCITY_CONVERSION;
            currentCommandedRPM *= SmartDashboard.getNumber("DEBUG RPM CONV", 1.0d);

            flywheel.setVelocity(currentCommandedRPM);
        }
    }

    public static class Spool extends CommandBase {
        private Flywheel flywheel;
        private double velocity;

        public Spool(Flywheel flywheel, double velocity) {
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
            return Math.abs(flywheel.getVelocity() - velocity) <= 50.0d;
        }
    }

    public static class TuneFlywheel extends CommandBase {
        private SparkMaxTuningUtility tuner;

        public TuneFlywheel(Flywheel flywheel) {
            this.tuner = flywheel.getTuner();
        }

        @Override
        public void initialize() {
            tuner.tune();
        }

        @Override
        public void end(boolean interrupted) {
            tuner.stop();
        }
    }

    public static class ReverseBall extends CommandBase {
        private Flywheel flywheel;
        private Feeder feeder;

        public ReverseBall(Shooter shooter) {
            this.flywheel = shooter.getFlywheel();
            this.feeder = shooter.getFeeder();

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            flywheel.intake();
            feeder.intake();
        }
    }

    public static class PrepareBlindShot extends CommandBase {
        private final Flywheel flywheel;
        private final CargoKinematics cargoKinematics;

        private double currentCommandedRPM;

        public PrepareBlindShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = new CargoKinematics(s -> 0.306*s + 2.6, 0.5715, 2.6416);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            velocityControl(8.5d);
        }

        @Override
        public boolean isFinished() {
            return flywheel.isAtSetVelocity();
        }

        private void velocityControl(double velocity) {
            currentCommandedRPM = velocity * TANGENTIAL_VELOCITY_CONVERSION;
            currentCommandedRPM *= SmartDashboard.getNumber("DEBUG RPM CONV", 1.0d);

            flywheel.setVelocity(currentCommandedRPM);
        }
    }
}
