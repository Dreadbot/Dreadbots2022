package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.Solenoid;

public class Climber extends Subsystem {
    private final Solenoid leftNeutralHookActuator;
    private final Solenoid rightNeutralHookActuator;
    private final Solenoid climbingHookActuator;

    private final CANSparkMax winchMotor;
    private final SparkMaxPIDController winchPid;
    @SuppressWarnings("unused")
    private final RelativeEncoder winchEncoder;

    public Climber(Solenoid leftNeutralHookActuator, Solenoid rightNeutralHookActuator, Solenoid climbingHookActuator,
            CANSparkMax winchMotor) {
        super("Climber");

        this.leftNeutralHookActuator = leftNeutralHookActuator;
        this.rightNeutralHookActuator = rightNeutralHookActuator;
        this.climbingHookActuator = climbingHookActuator;

        this.winchMotor = winchMotor;
        this.winchPid = winchMotor.getPIDController();
        this.winchEncoder = winchMotor.getEncoder();

        winchPid.setP(0.1);
        winchPid.setI(0);
        winchPid.setD(0);
        winchPid.setIZone(0);
        winchPid.setFF(0.000015);
        winchPid.setOutputRange(-1.0, 1.0);
        winchMotor.getFirmwareVersion();
        winchMotor.restoreFactoryDefaults();
        winchMotor.setIdleMode(IdleMode.kBrake);
    }

    @Override
    public void close() throws Exception {
        leftNeutralHookActuator.close();
        rightNeutralHookActuator.close();
        climbingHookActuator.close();

        winchMotor.close();
        winchMotor.close();
    }

    @Override
    protected void stopMotors() {
        winchMotor.stopMotor();
        winchMotor.stopMotor();
    }

    public void rotateClimbingHookVertical() {
        climbingHookActuator.set(true);
    }

    public void rotateClimbingHookDown() {
        climbingHookActuator.set(false);
    }

    public void rotateNeutralHooksVertical() {
        leftNeutralHookActuator.set(true);
        rightNeutralHookActuator.set(true);
    }

    public void rotateNeutralHooksDown() {
        leftNeutralHookActuator.set(false);
        rightNeutralHookActuator.set(false);
    }

    public void extendArm(double distance) {
        winchPid.setReference(distance, ControlType.kPosition);
    }
}
