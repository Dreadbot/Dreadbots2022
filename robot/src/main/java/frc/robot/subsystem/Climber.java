package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Solenoid;

public class Climber {
    private final Solenoid shortHookSolenoid;
    private final Solenoid longHookSolenoid;
    private final CANSparkMax leftMotor;
    private final CANSparkMax rightMotor;
    public Climber( Solenoid shortHookSolenoid, Solenoid longHookSolenoid, CANSparkMax leftMotor, CANSparkMax rightMotor){
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.shortHookSolenoid = shortHookSolenoid;
        this.longHookSolenoid = longHookSolenoid;
    }
}
