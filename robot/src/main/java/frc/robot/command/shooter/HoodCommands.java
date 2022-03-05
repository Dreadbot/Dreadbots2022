package frc.robot.command.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Hood;
import frc.robot.util.CargoKinematics;
import frc.robot.util.VisionInterface;

public class HoodCommands {
    private static final PIDController hoodTrackingController = new PIDController(1.0d, 0.0d, 0.0d);
    static {
        hoodTrackingController.setSetpoint(0.0d);
        hoodTrackingController.disableContinuousInput();
        hoodTrackingController.setTolerance(1.0d);

        SmartDashboard.putData("HoodTrackingPID", hoodTrackingController);
    }

    public static class PassiveTrack extends CommandBase {
        private final Hood hood;
        private final CargoKinematics cargoKinematics;

        private double lastDistanceToHub;

        public PassiveTrack(Hood hood) {
            this.hood = hood;
            this.cargoKinematics = new CargoKinematics(s -> 3.048, 0.5715, 2.6416);

            addRequirements(hood);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double distanceToHub = VisionInterface.getRelativeDistanceToHub();
            double hoodAngle = cargoKinematics.getBallDirectionAngle(distanceToHub);

            if(distanceToHub != lastDistanceToHub) hoodControlAngle(hoodAngle);
            lastDistanceToHub = distanceToHub;
        }

        private void hoodControlAngle(double relativeHoodAngle) {
            double currentAngle = hood.getAngle();
            double requestedAngle = currentAngle - hoodTrackingController.calculate(relativeHoodAngle);

            hood.setAngle(requestedAngle);
        }
    }

    public static class ActiveTrack extends CommandBase {
        private final Hood hood;
        private final CargoKinematics cargoKinematics;

        private double lastDistanceToHub;

        public ActiveTrack(Hood hood) {
            this.hood = hood;
            this.cargoKinematics = new CargoKinematics(s -> 3.048, 0.5715, 2.6416);

            addRequirements(hood);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double distanceToHub = VisionInterface.getRelativeDistanceToHub();
            double hoodAngle = cargoKinematics.getBallDirectionAngle(distanceToHub);

            if(distanceToHub != lastDistanceToHub) hoodControlAngle(hoodAngle);
            lastDistanceToHub = distanceToHub;
        }

        @Override
        public boolean isFinished() {
            return hoodTrackingController.atSetpoint();
        }

        private void hoodControlAngle(double relativeHoodAngle) {
            double currentAngle = hood.getAngle();
            double requestedAngle = currentAngle - hoodTrackingController.calculate(relativeHoodAngle);

            hood.setAngle(requestedAngle);
        }
    }

    public static class Calibrate extends CommandBase {
        private final Hood hood;

        private boolean fullCalibration;

        private boolean lowerCalibrated = false;
        private boolean upperCalibrated = false;

        public Calibrate(Hood hood) {
            this.hood = hood;
            this.fullCalibration = true;

            addRequirements(hood);
        }

        public Calibrate(Hood hood, boolean fullCalibration) {
            this.hood = hood;
            this.fullCalibration = fullCalibration;

            addRequirements(hood);
        }

        @Override
        public void initialize() {
            lowerCalibrated = false;
            upperCalibrated = false;
        }

        @Override
        public void execute() {
            if(!lowerCalibrated) {
                calibrateLower();
                return;
            }

            if(fullCalibration) {
                calibrateUpper();
                return;
            }

            hood.setUpperMotorLimit(hood.getPosition() + Constants.HOOD_RANGE);
            upperCalibrated = true;
        }

        private void calibrateUpper() {
            hood.setSpeed(-Constants.HOOD_CALIBRATION_SPEED);

            if(!hood.getUpperLimitSwitch()) return;
            hood.stopMotors();
            hood.setUpperMotorLimit(hood.getPosition());
            upperCalibrated = true;
        }

        private void calibrateLower() {
            hood.setSpeed(Constants.HOOD_CALIBRATION_SPEED);

            if(!hood.getLowerLimitSwitch()) return;
            hood.stopMotors();
            hood.setLowerMotorLimit(hood.getPosition());
            lowerCalibrated = true;
        }

        @Override
        public boolean isFinished() {
            return upperCalibrated;
        }
    }

    private HoodCommands() {}

    public static class TurnToAngle extends CommandBase {
        private final Hood hood;
        private final double angle;

        public TurnToAngle(Hood hood, double angle) {
            this.hood = hood;
            this.angle = angle;

            addRequirements(hood);
        }

        @Override
        public void execute() {
            hood.setAngle(angle);
        }

        @Override
        public boolean isFinished() {
            return Math.abs(hood.getAngle() - angle) <= 1.0d;
        }
    }
}
