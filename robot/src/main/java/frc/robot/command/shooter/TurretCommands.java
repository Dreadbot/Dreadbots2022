package frc.robot.command.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    public static class Calibrate extends CommandBase {
        public static final double TURRET_CALIBRATION_SPEED = 0.3d;

        private final Turret turret;

        private boolean fullCalibration;

        private boolean lowerCalibrated = false;
        private boolean upperCalibrated = false;

        public Calibrate(Turret turret) {
            this.turret = turret;
            this.fullCalibration = true;

            addRequirements(turret);
        }

        public Calibrate(Turret turret, boolean fullCalibration) {
            this.turret = turret;
            this.fullCalibration = fullCalibration;

            addRequirements(turret);
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
            turret.setSpeed(TURRET_CALIBRATION_SPEED);

            if(!turret.getUpperLimitSwitch()) return;
            turret.stopMotors();
            turret.setUpperMotorLimit(turret.getPosition());
            upperCalibrated = true;
        }

        private void calibrateLower() {
            turret.setSpeed(-TURRET_CALIBRATION_SPEED);

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

    public static class PassiveTrack extends CommandBase {
        private final Turret turret;
        private final PIDController pidController;

        private double lastRelativeAngleToHub;

        public PassiveTrack(Turret turret) {
            this.turret = turret;
            this.pidController = new PIDController(1.0d, 0.0d, 0.0d);

            pidController.setSetpoint(0.0d);
            pidController.disableContinuousInput();
            pidController.setTolerance(1.0d);
            SmartDashboard.putData("TurretTrackingPID", pidController);

            addRequirements(turret);
        }

        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            // Fetch current vision relative angle
            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub();
            if(relativeAngleToHub == lastRelativeAngleToHub) return;

            // Calculate the commanded absolute angle from relative
            double currentTurretAngle = turret.getAngle();
            double requestedAngle = currentTurretAngle - pidController.calculate(relativeAngleToHub);

            // Command hardware and update state
            turret.setAngle(requestedAngle);
            lastRelativeAngleToHub = relativeAngleToHub;

            SmartDashboard.putNumber("DEBUG RA", relativeAngleToHub);
            SmartDashboard.putNumber("DEBUG REQA", requestedAngle);
        }
    }

    private TurretCommands() { }
}
