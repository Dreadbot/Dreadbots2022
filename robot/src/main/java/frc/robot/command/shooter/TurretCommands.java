package frc.robot.command.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    private static final PIDController turretTrackingController = new PIDController(1.0d, 0.0d, 0.0d);
    static {
        turretTrackingController.setSetpoint(0.0d);
        turretTrackingController.disableContinuousInput();

        SmartDashboard.putData("TurretTrackingPID", turretTrackingController);
    }

    public static class PassiveTrack extends CommandBase {
        private final Turret turret;

        private double lastRelativeAngleToHub;

        public PassiveTrack(Turret turret) {
            this.turret = turret;

            addRequirements(turret);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub();

            if(relativeAngleToHub != lastRelativeAngleToHub) turretControlAngle(relativeAngleToHub);
            lastRelativeAngleToHub = relativeAngleToHub;
        }

        private void turretControlAngle(double relativeAngleToHub) {
            double currentAngle = turret.getAngle();
            double requestedAngle = currentAngle - turretTrackingController.calculate(relativeAngleToHub);

            turret.setAngle(requestedAngle);
        }
    }

    public static class ActiveTrack extends CommandBase {
        private final Turret turret;

        private double lastRelativeAngleToHub;

        public ActiveTrack(Turret turret) {
            this.turret = turret;

            addRequirements(turret);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub();

            if(relativeAngleToHub != lastRelativeAngleToHub) turretControlAngle(relativeAngleToHub);
            lastRelativeAngleToHub = relativeAngleToHub;
        }

        private void turretControlAngle(double relativeAngleToHub) {
            double currentAngle = turret.getAngle();
            double requestedAngle = currentAngle - turretTrackingController.calculate(relativeAngleToHub);

            turret.setAngle(requestedAngle);
        }
    }

    public static class Calibrate extends CommandBase {
        private final Turret turret;

        private final boolean fullCalibration;

        private boolean lowerCalibrated;
        private boolean upperCalibrated;

        public Calibrate(Turret turret, boolean fullCalibration) {
            this.turret = turret;
            this.fullCalibration = fullCalibration;

            addRequirements(turret);
        }

        @Override
        public void initialize() {
            this.lowerCalibrated = false;
            this.upperCalibrated = false;
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

            turret.setUpperMotorLimit(turret.getPosition() + Constants.TURRET_RANGE);
            upperCalibrated = true;
        }

        private void calibrateUpper() {
            turret.setSpeed(Constants.TURRET_CALIBRATION_SPEED);

            if(!turret.getUpperLimitSwitch()) return;
            turret.stopMotors();
            turret.setUpperMotorLimit(turret.getPosition());

            upperCalibrated = true;
        }

        private void calibrateLower() {
            turret.setSpeed(-Constants.TURRET_CALIBRATION_SPEED);

            if(!turret.getLowerLimitSwitch()) return;
            turret.stopMotors();
            turret.setLowerMotorLimit(turret.getPosition());

            lowerCalibrated = true;
        }

        @Override
        public boolean isFinished() {
            return upperCalibrated;
        }
    }

    private TurretCommands() { }
}










