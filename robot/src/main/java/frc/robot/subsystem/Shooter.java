package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

public class Shooter extends Subsystem {
    private final CANSparkMax flywheelMotor;
    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;

    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        super("Shooter");
        
        this.flywheelMotor = flywheelMotor;
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;
    }

    public void setTurretAngle(double turretAngle) {
        //TODO
    }

    public void shoot() {
        //TODO
    }

    @SuppressWarnings("unused")
    private void setHoodPosition(double hoodAngle) {
        //TODO
    }

    @SuppressWarnings("unused")
    private void setFlywheelRPM(double revolutionsPerMinute) {
        //TODO
    }
    
    @Override
    protected void stopMotors() {
        flywheelMotor.stopMotor();
        hoodMotor.stopMotor();
        turretMotor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        flywheelMotor.close();
        hoodMotor.close();
        turretMotor.close();
    }
    
    public CANSparkMax getFlywheelMotor() {
        return flywheelMotor;
    }

    public CANSparkMax getHoodMotor() {
        return hoodMotor;
    }

    public CANSparkMax getTurretMotor() {
        return turretMotor;
    }
}
