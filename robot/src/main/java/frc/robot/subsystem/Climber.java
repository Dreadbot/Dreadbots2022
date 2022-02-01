package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.Solenoid;

public class Climber extends Subsystem{
    private final Solenoid leftNeutralHookActuator;
    private final Solenoid rightNeutralHookActuator;

    private final Solenoid leftClimbingHookActuator;
    private final Solenoid rightClimbingHookActuator;

    private final CANSparkMax leftWinchMotor;
    private final CANSparkMax rightWinchMotor;

    public Climber(Solenoid leftNeutralHookActuator, Solenoid rightNeutralHookActuator,
            Solenoid leftClimbingHookActuator, Solenoid rightClimbingHookActuator, CANSparkMax leftWinchMotor,
            CANSparkMax rightWinchMotor) {
        super("Climber");
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

    @Override
    public void close() throws Exception {
        leftNeutralHookActuator.close();
        rightNeutralHookActuator.close();
        leftClimbingHookActuator.close();
        rightClimbingHookActuator.close();
        leftWinchMotor.close();
        rightWinchMotor.close();
        
    }

    @Override
    protected void stopMotors() {
        leftWinchMotor.stopMotor();
        rightWinchMotor.stopMotor();
    }


    public void rotateLeftClimbingHookVertical() {
        leftClimbingHookActuator.set(true);
    }

    public void rotateRightClimbingHookVertical() {
        rightClimbingHookActuator.set(true);
    }
    public void rotateLeftClimbingHookDown() {
        leftClimbingHookActuator.set(false);
    }

    public void rotateRightClimbingHookDown() {
        rightClimbingHookActuator.set(false);
    }


    public void rotateLeftNeutralHookVertical() {
        leftNeutralHookActuator.set(true);
    }

    public void rotateRightNeutralHookVertical() {
        rightNeutralHookActuator.set(true);
    }
    public void rotateLeftNeutralHookDown() {
        leftNeutralHookActuator.set(false);
    }

    public void rotateRightNeutralHookDown() {
        rightNeutralHookActuator.set(false);
    }

    public void extendArm(int distance) {
        //TODO
    }
    public void retractArm(int distance) {
        //TODO
    }
}
