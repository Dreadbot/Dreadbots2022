package frc.robot.subsystem.shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.DreadbotMath;
import frc.robot.util.MotorSafeSystem;

public class Turret extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    enum TurretCalibrationState {
        NotCalibrated,
        CalibratedLeft,
        CalibratedRight,
        Done
    }

    private final DigitalInput leftSwitch;
    private final DigitalInput rightSwitch;
    private final CANSparkMax turretMotor;
    private RelativeEncoder turretEncoder;
    private SparkMaxPIDController turretPIDController;

    private TurretCalibrationState calibrationState;
    private double motorLowerLimit = 0.0;
    private double motorUpperLimit = 0.0;

    public Turret(CANSparkMax turretMotor, DigitalInput leftSwitch, DigitalInput rightSwitch) {
        this.leftSwitch = leftSwitch;
        this.rightSwitch = rightSwitch;
        this.turretMotor = turretMotor;

        if(!Constants.TURRET_ENABLED) {
            leftSwitch.close();
            rightSwitch.close();
            turretMotor.close();

            return;
        }

        turretMotor.setIdleMode(IdleMode.kBrake);
        turretEncoder = turretMotor.getEncoder();
        turretPIDController = turretMotor.getPIDController();
        
        turretPIDController.setP(0.1);
        turretPIDController.setI(0.0);
        turretPIDController.setD(0);
        turretPIDController.setIZone(0);
        turretPIDController.setFF(0.000015);
        turretPIDController.setOutputRange(-.1, .1);
        this.calibrationState = TurretCalibrationState.NotCalibrated;
        SmartDashboard.putBoolean("left", getLeftLimitSwitch());
        SmartDashboard.putBoolean("right", getRightLimitSwitch());
    }

    public void setAngle(double angle) {
        if(!Constants.TURRET_ENABLED) return; 

        // TODO Add conversion from angle to rotations
        double rotations = angle;

        rotations = DreadbotMath.clampValue(rotations, motorLowerLimit, motorUpperLimit);
        turretPIDController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setSpeed(double speed) {
        if(!Constants.TURRET_ENABLED) return;

        speed = DreadbotMath.clampValue(speed, -0.1, 0.1);
        turretMotor.set(speed);
    }

    public boolean getLeftLimitSwitch() {
        if(!Constants.TURRET_ENABLED) return false;

        return !leftSwitch.get();
    }

    public boolean getRightLimitSwitch() {
        if(!Constants.TURRET_ENABLED) return false;

        return !rightSwitch.get();
    }

    public double getAngle() {
        if(!Constants.TURRET_ENABLED) return 0.0d;

        double rotations = turretEncoder.getPosition();

        double angle = rotations;

        return angle;
    }

    public void switchDebug() {
        if(!Constants.TURRET_ENABLED) return; 

        SmartDashboard.putBoolean("left", getLeftLimitSwitch());
        SmartDashboard.putBoolean("right", getRightLimitSwitch());
    }

    public void calibrateTurret() {
        if(!Constants.TURRET_ENABLED) return; 

        double targetPosition = ((motorUpperLimit - motorLowerLimit)/2) + motorLowerLimit;
        if(calibrationState == TurretCalibrationState.Done){
            SmartDashboard.putNumber("Target Position", targetPosition);
            SmartDashboard.putNumber("Actual position", turretEncoder.getPosition());
            return;
        }

        SmartDashboard.putNumber("LowerLimit", motorLowerLimit);
        SmartDashboard.putNumber("UpperLimit", motorUpperLimit);
        if(calibrationState == TurretCalibrationState.NotCalibrated) {
            if(!getRightLimitSwitch()){
                turretMotor.set(-.1);
            }
            else{
                turretMotor.set(0);
                motorLowerLimit = turretEncoder.getPosition();
                calibrationState = TurretCalibrationState.CalibratedLeft;
            }
        }
        else if(calibrationState == TurretCalibrationState.CalibratedLeft){
            if(!getLeftLimitSwitch()){
                turretMotor.set(.1);
            }
            else{
                turretMotor.set(0);
                motorUpperLimit = turretEncoder.getPosition();
                calibrationState = TurretCalibrationState.CalibratedRight;
            }
        }
        else if (calibrationState == TurretCalibrationState.CalibratedRight){
            System.out.println("Done");
            turretPIDController.setReference(targetPosition, CANSparkMax.ControlType.kPosition);
            calibrationState = TurretCalibrationState.Done;
        }
    }

    @Override
    public void close() throws Exception {
        leftSwitch.close();
        rightSwitch.close();
    }

    @Override
    public void stopMotors() {
        if(!Constants.TURRET_ENABLED) return; 

        turretMotor.stopMotor();
    }
}