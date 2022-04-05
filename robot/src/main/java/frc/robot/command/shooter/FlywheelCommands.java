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
            // In case the vision isn't seeing the ball, don't continue to feed flywheel
            // This is SUPER IMPORTANT.
            if(!VisionInterface.canTrackHub()) return;

//            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
            double distanceToHub = VisionInterface.getRelativeDistanceToHub();
//            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            commandedVelocity = 14.0d;
            SmartDashboard.putNumber("OUT VIP TEMP", commandedVelocity);
            flywheel.setVelocity(commandedVelocity);
        }

        @Override
        public boolean isFinished() {
            return flywheel.isAtSetVelocity();

//            return Math.abs(flywheel.getTangentialVelocity() - commandedVelocity) <= 0.15d;
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
