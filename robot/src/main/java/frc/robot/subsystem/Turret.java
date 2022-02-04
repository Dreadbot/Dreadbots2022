package frc.robot.subsystem;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    private TurretCalibrationState calibrationState;
    public Turret(DigitalInput leftSwitch, DigitalInput rightSwitch, CANSparkMax turretMotor) {
        super("Turret");
        this.leftSwitch = leftSwitch;
        this.rightSwitch = rightSwitch;
        this.turretMotor = turretMotor;
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

    public void calibrateTurret() {
        if(calibrationState == TurretCalibrationState.NotCalibrated) {
            if(!getRightLimitSwitch()){
                turretMotor.set(.1);
            }
            else{
                calibrationState = TurretCalibrationState.CalibratedLeft;
            }
        }
    }
}