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
        private CargoKinematics cargoKinematics;

        private double commandedVelocity;

        public PrepareVisionShot(Flywheel flywheel) {
            this.flywheel = flywheel;
            this.cargoKinematics = flywheel.getCargoKinematics();

            SmartDashboard.putNumber("TUNING ARC HEIGHT", 1.0d);

            addRequirements(flywheel);
        }

        @Override
        public void execute() {
            // In case the vision isn't seeing the ball, don't continue to feed flywheel
            // This is SUPER IMPORTANT.
            if(!VisionInterface.canTrackHub()) return;

////            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
//            double d = VisionInterface.getRelativeDistanceToHub();
////            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);
//
//            // Quadratic Regression
//            double commandedVelocity = 0.000514d * d * d + -0.108872d * d + 18.9667d;
////            double commandedVelocity = 0.000514d * d * d + -0.108872d * d + 17.9667d;
//
////            commandedVelocity = SmartDashboard.getNumber("TUNING FLYWHEEL SPEED", 3.0d);
//            flywheel.setVelocity(commandedVelocity);

            //cargoKinematics = new CargoKinematics(s-> SmartDashboard.getNumber("TUNING ARC HEIGHT", 1.0d), 0.5715, 2.6416);
            double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
            double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);

            commandedVelocity = velocity; //* SmartDashboard.getNumber("TUNING ARC HEIGHT", 1.0d);
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
