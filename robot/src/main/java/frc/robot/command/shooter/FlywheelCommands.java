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

        private double commandedVelocity;

        public PrepareVisionShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = new CargoKinematics(s -> 3.2, 0.5715, 2.6416);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
//            SmartDashboard.putNumber("VS DistanceToHubMeters", distanceToHub);
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);
//            SmartDashboard.putNumber("VS FinalCommandVelocity", velocity);

            commandedVelocity = velocity;
            SmartDashboard.putNumber("OUT VIP TEMP", velocity);
            flywheel.setVelocity(velocity * 4);
        }

        @Override
        public boolean isFinished() {
            return flywheel.getTangentialVelocity() >= commandedVelocity;
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
            return Math.abs(flywheel.getTangentialVelocity() - velocity) <= 1.0d;
        }
    }
}
