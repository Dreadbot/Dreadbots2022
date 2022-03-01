package frc.robot.command.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Hood;

public class HoodCommands {
    public static class Calibrate extends CommandBase {
        public static final double HOOD_CALIBRATION_SPEED = 0.7d;

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
            hood.setSpeed(HOOD_CALIBRATION_SPEED);

            if(!hood.getUpperLimitSwitch()) return;
            hood.stopMotors();
            hood.setUpperMotorLimit(hood.getPosition());
            upperCalibrated = true;
        }

        private void calibrateLower() {
            hood.setSpeed(-HOOD_CALIBRATION_SPEED);

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
}
