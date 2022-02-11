package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.DreadbotMath;
import frc.robot.util.MotorSafeSystem;

public class Hood extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    enum HoodCalibrationState {
        NotCalibrated,
        CalibratedLower,
        CalibratedUpper,
        Done
    }

    private final DigitalInput lowerSwitch;
    private final DigitalInput upperSwitch;
    private final CANSparkMax hoodMotor;
    private RelativeEncoder hoodEncoder;
    private SparkMaxPIDController hoodPIDController;

    private HoodCalibrationState calibrationState;
    private double motorLowerLimit = 0.0;
    private double motorUpperLimit = 0.0;

    public Hood (CANSparkMax hoodMotor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.hoodMotor = hoodMotor;
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;

        if(!Constants.HOOD_ENABLED) {
            lowerSwitch.close();
            upperSwitch.close();
            hoodMotor.close();

            return;
        }

        hoodMotor.setIdleMode(IdleMode.kCoast);
        hoodEncoder = hoodMotor.getEncoder();
        hoodPIDController = hoodMotor.getPIDController();
         
        hoodPIDController.setP(0.1); // Change numbers maybe
        hoodPIDController.setI(0); 
        hoodPIDController.setD(0);
        hoodPIDController.setIZone(0);
        hoodPIDController.setFF(0.000015);
        hoodPIDController.setOutputRange(-.5, .5);
        this.calibrationState = HoodCalibrationState.NotCalibrated;
        SmartDashboard.putBoolean("lower", getLowerLimitSwitch());
        SmartDashboard.putBoolean("upper", getUpperLimitSwitch());
    }

    public void setAngle(double angle) {
        if(!Constants.HOOD_ENABLED) return; 

        // TODO Add conversion from angle to rotations
        double rotations = angle;

        rotations = DreadbotMath.clampValue(rotations, motorLowerLimit, motorUpperLimit);
        hoodPIDController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setSpeed(double speed) {
        if(!Constants.HOOD_ENABLED) return;

        speed = DreadbotMath.clampValue(speed, -0.1, 0.1);
        hoodMotor.set(speed);
    }

    public boolean getUpperLimitSwitch() {
        if(!Constants.HOOD_ENABLED) return false;

        return !upperSwitch.get();
    }

    public boolean getLowerLimitSwitch() {
        if(!Constants.HOOD_ENABLED) return false;

        return !lowerSwitch.get();
    }

    public void calibrateHood() {
        if(!Constants.HOOD_ENABLED) return;

        double targetPosition = ((motorUpperLimit - motorLowerLimit)/2) + motorLowerLimit;
        if(calibrationState == HoodCalibrationState.Done){
            SmartDashboard.putNumber("Target Position", targetPosition);
            SmartDashboard.putNumber("Actual position", hoodEncoder.getPosition());
            return;
        }

        SmartDashboard.putNumber("LowerLimit", motorLowerLimit);
        SmartDashboard.putNumber("UpperLimit", motorUpperLimit);
        if(calibrationState == HoodCalibrationState.NotCalibrated) {
            if(!getUpperLimitSwitch()){
                hoodMotor.set(-.1);
            }
            else{
                hoodMotor.set(0);
                motorLowerLimit = hoodEncoder.getPosition();
                calibrationState = HoodCalibrationState.CalibratedLower;
            }
        }
        else if(calibrationState == HoodCalibrationState.CalibratedLower){
            if(!getLowerLimitSwitch()){
                hoodMotor.set(.1);
            }
            else{
                hoodMotor.set(0);
                motorUpperLimit = hoodEncoder.getPosition();
                calibrationState = HoodCalibrationState.CalibratedUpper;
            }
        }
        else if (calibrationState == HoodCalibrationState.CalibratedUpper){
            System.out.println("Done");
            hoodPIDController.setReference(targetPosition, CANSparkMax.ControlType.kPosition);
            calibrationState = HoodCalibrationState.Done;
        }
    }

    @Override
    public void close() throws Exception {
        lowerSwitch.close();
        upperSwitch.close();
    }

    @Override
    public void stopMotors() {
        if(!Constants.HOOD_ENABLED) return;

        hoodMotor.stopMotor();
    }
}
