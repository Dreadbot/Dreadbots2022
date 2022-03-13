package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.RobotControlMode;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    private static RobotControlMode turretControlMode;
    static {
        turretControlMode = RobotControlMode.AUTOMATIC;
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
            double requestedAngle = currentAngle + relativeAngleToHub;

            turret.setAngle(requestedAngle);
        }
    }

    public static class ActiveTrack extends CommandBase {
        private final Turret turret;

        private double lastRelativeAngleToHub;

        private double requestedAngle;

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

        @Override
        public boolean isFinished() {
            return turret.isAtSetAngle();
        }

        private void turretControlAngle(double relativeAngleToHub) {
            double currentAngle = turret.getAngle();
            requestedAngle = currentAngle + relativeAngleToHub;

            turret.setAngle(requestedAngle);
        }
    }

    public static class EjectTrack extends CommandBase {
        private final Turret turret;

        private double lastRelativeAngleToHub;
        private double requestedAngle;

        public EjectTrack(Turret turret) {
            this.turret = turret;

            addRequirements(turret);
        }

        @Override
        public void execute() {
//            if(!VisionInterface.canTrackHub()) return;

            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub() + 45;

            if(relativeAngleToHub != lastRelativeAngleToHub) turretControlAngle(relativeAngleToHub);
            lastRelativeAngleToHub = relativeAngleToHub;
        }

        @Override
        public boolean isFinished() {
            return turret.isAtSetAngle();
        }

        private void turretControlAngle(double relativeAngleToHub) {
            double currentAngle = turret.getAngle();
            requestedAngle = currentAngle + relativeAngleToHub;

            turret.setAngle(requestedAngle);
        }
    }

    public static class TurnToAngle extends CommandBase {
        private final Turret turret;
        private final double angle;

        public TurnToAngle(Turret turret, double angle) {
            this.turret = turret;
            this.angle = angle;

            addRequirements(turret);
        }

        @Override
        public void execute() {
            turret.setAngle(angle);
        }

        @Override
        public boolean isFinished() {
            return turret.isAtSetAngle();
        }
    }

    public static class TurnToRelativeAngle extends CommandBase {
        private final Turret turret;
        private final double angle;

        private double initialTurretAngle;

        public TurnToRelativeAngle(Turret turret, double angle) {
            this.turret = turret;
            this.angle = angle;

            addRequirements(turret);
        }

        /**
         * The initial subroutine of a command. Called once when the command is initially scheduled.
         */
        @Override
        public void initialize() {
            this.initialTurretAngle = turret.getAngle();
        }

        @Override
        public void execute() {
            turret.setAngle(initialTurretAngle + angle);
        }

        @Override
        public boolean isFinished() {
            return turret.isAtSetAngle();
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

    public static void swapManualAutomaticControls() {
        if(turretControlMode == RobotControlMode.AUTOMATIC) {
            turretControlMode = RobotControlMode.MANUAL;
        } else {
            turretControlMode = RobotControlMode.AUTOMATIC;
        }

        SmartDashboard.putBoolean("TURRET AUTOMATION", turretControlMode == RobotControlMode.AUTOMATIC);
    }
}










