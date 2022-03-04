package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Flywheel;
import frc.robot.util.CargoKinematics;
import frc.robot.util.VisionInterface;

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
            this.cargoKinematics = new CargoKinematics(s -> 3.048, 0.5715, 2.6416);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double distanceToHub = VisionInterface.getRelativeDistanceToHub();
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            if(distanceToHub != lastDistanceToHub) velocityControl(velocity);
            lastDistanceToHub = distanceToHub;
        }

        @Override
        public boolean isFinished() {
            return Math.abs(currentCommandedRPM - flywheel.getVelocity()) <= 20.0d;
        }

        private void velocityControl(double velocity) {
            currentCommandedRPM = velocity * TANGENTIAL_VELOCITY_CONVERSION;
            currentCommandedRPM *= SmartDashboard.getNumber("DEBUG RPM CONV", 1.0d);

            flywheel.setVelocity(currentCommandedRPM);
        }
    }
}
