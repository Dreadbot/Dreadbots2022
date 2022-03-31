package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.controls.VisionInterface;

public class TurretCommands {
    public static class PassiveTrack extends CommandBase {
        private final Turret turret;
        private final Drive drive;

        private double lastRelativeAngleToHub;
        private double lastRobotAngle = 0.0d;

        public PassiveTrack(Turret turret, Drive drive) {
            this.turret = turret;
            this.drive = drive;

            addRequirements(turret);
        }

        @Override
        public void initialize() {
            lastRobotAngle = drive.getYaw();
        }

        @Override
        public void execute() {
            double relativeAngleToHub = 0.0d;

            // Vision Corrections
            relativeAngleToHub += VisionInterface.canTrackHub() ? VisionInterface.getRelativeAngleToHub() : 0.0d;

            // Gyro corrections
//            relativeAngleToHub -= lastRobotAngle - drive.getYaw();

            if(relativeAngleToHub != lastRelativeAngleToHub) turretControlAngle(relativeAngleToHub);

            lastRelativeAngleToHub = relativeAngleToHub;
            lastRobotAngle = drive.getYaw();
        }

        private void turretControlAngle(double relativeAngleToHub) {
            double currentAngle = turret.getAngle();
            double requestedAngle = currentAngle + relativeAngleToHub;

            turret.setAngle(requestedAngle);
        }
    }

    public static class EjectShootPreset extends CommandBase {
        private Shooter shooter;
        private double angleOnScore;
        private double angleOnEject;

        public EjectShootPreset(Shooter shooter, double angleOnScore, double angleOnEject) {
            this.shooter = shooter;

            this.angleOnScore = angleOnScore;
            this.angleOnEject = angleOnEject;

            addRequirements(shooter.getTurret());
        }

        @Override
        public void initialize() {
            SmartDashboard.putString("CurrentLowShootCommand", "Configuring Shooter");
        }

        @Override
        public void execute() {
            shooter.getTurret().setAngle(getAngle());
        }

        @Override
        public boolean isFinished() {
            return shooter.getTurret().isAtSetAngle();
        }

        private double getAngle() {
            if(!shooter.getColorSensor().isCorrectColor() &&
                shooter.getColorSensor().getBallColor() != null) return angleOnEject;

            return angleOnScore;
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
            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub() + 20;

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
}










