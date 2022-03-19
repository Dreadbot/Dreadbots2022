package frc.robot.command.shooter;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Flywheel;
import frc.robot.util.CargoKinematics;
import frc.robot.util.VisionInterface;

public class FlywheelCommands {
    public static class PrepareVisionShot extends CommandBase {
        private final Flywheel flywheel;
        private final CargoKinematics cargoKinematics;

        private double lastDistanceToHub;
        private double lastVelocity;

        public PrepareVisionShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = new CargoKinematics(s -> 0.306*s + 2.6, 0.5715, 2.6416);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            if(distanceToHub != lastDistanceToHub) velocityControl(velocity);
            lastDistanceToHub = distanceToHub;
        }

        @Override
        public boolean isFinished() {
            return Math.abs(flywheel.getTangentialVelocity() - lastVelocity) <= 1.0d;
        }

        private void velocityControl(double velocity) {
            lastVelocity = velocity;

            flywheel.setVelocity(velocity);
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
            return Math.abs(flywheel.getTangentialVelocity() - velocity) <= 0.1d;
        }
    }
}
