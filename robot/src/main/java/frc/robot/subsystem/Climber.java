package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Climber extends Subsystem {
    private final Solenoid leftNeutralHookActuator;
    // private final Solenoid rightNeutralHookActuator;
    private final Solenoid climbingHookActuator;
    private final CANSparkMax winchMotor;
    private double retractedPosition;
    private SparkMaxPIDController winchPid;
    @SuppressWarnings("unused")
    private RelativeEncoder winchEncoder;

    public Climber(Solenoid leftNeutralHookActuator, Solenoid climbingHookActuator, CANSparkMax winchMotor) {
        super("Climber");
        this.leftNeutralHookActuator = leftNeutralHookActuator;
        this.climbingHookActuator = climbingHookActuator;
        this.winchMotor = winchMotor;

        if(!Constants.CLIMB_ENABLED) {
            leftNeutralHookActuator.close();
            // rightNeutralHookActuator.close();
            climbingHookActuator.close();
      
            winchMotor.close();

            return;
        }
        winchMotor.restoreFactoryDefaults();
        this.winchPid = winchMotor.getPIDController();
        this.winchEncoder = winchMotor.getEncoder();
        SmartDashboard.putNumber("WinchPosition", 0);
        winchPid.setP(0.1);
        winchPid.setI(0);
        winchPid.setD(0);
        winchPid.setIZone(0);
        winchPid.setFF(0.000015);
        winchPid.setOutputRange(-1.0, 1.0);
        winchMotor.getFirmwareVersion();
        winchMotor.setIdleMode(IdleMode.kBrake);
        this.retractedPosition = winchEncoder.getPosition();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.CLIMB_ENABLED) return;

        leftNeutralHookActuator.close();
        // rightNeutralHookActuator.close();
        climbingHookActuator.close();

        winchMotor.close();
        winchMotor.close();
    }

    @Override
    protected void stopMotors() {
        if(!Constants.CLIMB_ENABLED) return;

        winchMotor.stopMotor();
        winchMotor.stopMotor();
    }

    public void rotateClimbingHookVertical() {
        if(!Constants.CLIMB_ENABLED) return;

        climbingHookActuator.set(true);
    }

    public void rotateClimbingHookDown() {
        if(!Constants.CLIMB_ENABLED) return;

        climbingHookActuator.set(false);
    }

    public void rotateNeutralHooksVertical() {
        if(!Constants.CLIMB_ENABLED) return;

        leftNeutralHookActuator.set(true);
        // rightNeutralHookActuator.set(true);
    }

    public void rotateNeutralHooksDown() {
        if(!Constants.CLIMB_ENABLED) return;

        leftNeutralHookActuator.set(false);
        // rightNeutralHookActuator.set(false);
    }

    public void setWinch(double factor) {
        if(!Constants.CLIMB_ENABLED) return;
        winchMotor.set(factor);
    }

    public void extendArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference(Constants.MAX_ARM_DISTANCE - retractedPosition, ControlType.kPosition);
    }
    public void retractArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference(retractedPosition, ControlType.kPosition);
    }
    public void halfExtendArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference((Constants.MAX_ARM_DISTANCE - retractedPosition) * 0.5, ControlType.kPosition);
    }
}
