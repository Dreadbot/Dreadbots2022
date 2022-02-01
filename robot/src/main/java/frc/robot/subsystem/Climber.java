package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.Solenoid;

public class Climber {
    private final Solenoid leftNeutralHookActuator;
    private final Solenoid rightNeutralHookActuator;

    private final Solenoid leftClimbingHookActuator;
    private final Solenoid rightClimbingHookActuator;

    private final CANSparkMax leftWinchMotor;
    private final CANSparkMax rightWinchMotor;

    public Climber(Solenoid leftNeutralHookActuator, Solenoid rightNeutralHookActuator,
            Solenoid leftClimbingHookActuator, Solenoid rightClimbingHookActuator, CANSparkMax leftWinchMotor,
            CANSparkMax rightWinchMotor) {
        this.leftNeutralHookActuator = leftNeutralHookActuator;
        this.rightNeutralHookActuator = rightNeutralHookActuator;
        this.leftClimbingHookActuator = leftClimbingHookActuator;
        this.rightClimbingHookActuator = rightClimbingHookActuator;
        this.leftWinchMotor = leftWinchMotor;
        this.rightWinchMotor = rightWinchMotor;

        leftWinchMotor.getFirmwareVersion();
        
        leftWinchMotor.restoreFactoryDefaults();
        leftWinchMotor.setIdleMode(IdleMode.kBrake);
        
        rightWinchMotor.restoreFactoryDefaults();
        rightWinchMotor.setIdleMode(IdleMode.kBrake);
        rightWinchMotor.setInverted(true);
    }
}
