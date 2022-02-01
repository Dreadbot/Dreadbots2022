package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.Solenoid;

public class Climber extends Subsystem{
    private final Solenoid leftNeutralHookActuator;
    private final Solenoid rightNeutralHookActuator;

    private final Solenoid leftClimbingHookActuator;
    private final Solenoid rightClimbingHookActuator;

    private final CANSparkMax leftWinchMotor;
    private final CANSparkMax rightWinchMotor;
    private final SparkMaxPIDController leftWinchPid;
    private final RelativeEncoder leftWinchEncoder;
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
        this.leftWinchPid = this.leftWinchMotor.getPIDController();
        this.leftWinchEncoder = this.leftWinchMotor.getEncoder();
        leftWinchPid.setP(0.1);
        leftWinchPid.setI(0);
        leftWinchPid.setD(0);
        leftWinchPid.setIZone(0);
        leftWinchPid.setFF(0.000015);
        leftWinchPid.setOutputRange(-1.0, 1.0);
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
        leftWinchPid.setReference((double) distance, ControlType.kPosition);
        
    }
    public void retractArm(int distance) {
        leftWinchPid.setReference(-1 * (double) distance, ControlType.kPosition);
    }
}
