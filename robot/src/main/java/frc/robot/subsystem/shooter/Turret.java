package frc.robot.subsystem.shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.Subsystem;
import frc.robot.util.DreadbotMath;

public class Turret extends Subsystem {
    enum TurretCalibrationState {
        NotCalibrated,
        CalibratedLeft,
        CalibratedRight,
        Done
    }

    private DigitalInput leftSwitch;
    private DigitalInput rightSwitch;
    private CANSparkMax turretMotor;
    private RelativeEncoder turretEncoder;
    private SparkMaxPIDController turretPIDController;

    private TurretCalibrationState calibrationState;
    private double motorLowerLimit = 0.0;
    private double motorUpperLimit = 0.0;

    public Turret(DigitalInput leftSwitch, DigitalInput rightSwitch, CANSparkMax turretMotor) {
        super("Turret");
        this.leftSwitch = leftSwitch;
        this.rightSwitch = rightSwitch;
        this.turretMotor = turretMotor;
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

    @Override
    public void close() throws Exception {
        leftSwitch.close();
        rightSwitch.close();
    }
    public boolean getLeftLimitSwitch() {
        return !leftSwitch.get();
    }

    public boolean getRightLimitSwitch() {
        return !rightSwitch.get();
    }
    public void switchDebug() {
        SmartDashboard.putBoolean("left", getLeftLimitSwitch());
        SmartDashboard.putBoolean("right", getRightLimitSwitch());
    }
    @Override
    protected void stopMotors() {
        // TODO Auto-generated method stub
        
    }

    public void turnToRotation(double rotations) {
        rotations = DreadbotMath.clampValue(rotations, motorLowerLimit, motorUpperLimit);
        turretPIDController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void calibrateTurret() {
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
}