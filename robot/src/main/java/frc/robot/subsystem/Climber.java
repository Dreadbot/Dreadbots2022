package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Climber extends Subsystem {
    public enum ClimberState {
        Idle,
        ArmUp,
        ReachBarStart,
        RetractArm,
        HookUp,
        ExtendHalfway,
        ArmBack,
        ReachBarEnd,
        RetractHalfway,
        ArmUpBarEnd
    }
    private final Solenoid leftNeutralHookActuator;
    // private final Solenoid rightNeutralHookActuator;
    private final Solenoid climbingHookActuator;
    private final CANSparkMax winchMotor;
    private double retractedPosition;
    private SparkMaxPIDController winchPid;
    private ClimberState state;
    private RelativeEncoder winchEncoder;
    private DigitalInput bottomLimitSwitch;
    private DigitalInput topLimitSwitch;

    public Climber(Solenoid leftNeutralHookActuator, Solenoid climbingHookActuator, CANSparkMax winchMotor, DigitalInput bottomLimitSwitch, DigitalInput topLimitSwitch) {
        super("Climber");
        this.leftNeutralHookActuator = leftNeutralHookActuator;
        this.climbingHookActuator = climbingHookActuator;
        this.winchMotor = winchMotor;
        this.bottomLimitSwitch = bottomLimitSwitch;
        this.topLimitSwitch = topLimitSwitch;
        this.state = ClimberState.Idle;
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
    private void setState(ClimberState state) {
        this.state = state;
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
    public boolean getBottomLimitSwitch() {
        return !bottomLimitSwitch.get();
    }
    public boolean getTopLimitSwitch() {
        return !topLimitSwitch.get();
    }
    public void retractArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference(Constants.MAX_ARM_DISTANCE - retractedPosition, ControlType.kPosition);
    }
    public void extendArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference(retractedPosition, ControlType.kPosition);
    }
    public void halfExtendArm() {
        if(!Constants.CLIMB_ENABLED) return;
        winchPid.setReference((Constants.MAX_ARM_DISTANCE - retractedPosition) * 0.5, ControlType.kPosition);
    }
    public void climbAutonomous(){
        if(!Constants.CLIMB_ENABLED) return;
        if(state == ClimberState.Idle) {
            rotateClimbingHookVertical();
            setState(ClimberState.ArmUp);
        } else if(state == ClimberState.ArmUp && climbingHookActuator.get()) {
            setState(ClimberState.ReachBarStart);
            if(!getTopLimitSwitch()) 
                extendArm();
        } else if(state == ClimberState.ReachBarStart) {
            if(Math.abs(Constants.MAX_ARM_DISTANCE - winchEncoder.getPosition()) <= 0.2) {
                if(!getBottomLimitSwitch())
                    retractArm();
                setState(ClimberState.RetractArm);
            }
        } else if(state == ClimberState.RetractArm) {
            if(Math.abs(retractedPosition - winchEncoder.getPosition()) <= 0.2) {
                setState(ClimberState.HookUp);
                rotateNeutralHooksVertical();
            }
        } else if(state == ClimberState.HookUp) {
            setState(ClimberState.ExtendHalfway);
            halfExtendArm();
        } else if(state == ClimberState.ExtendHalfway) {
            if(Math.abs(((Constants.MAX_ARM_DISTANCE - retractedPosition) * 0.5) - winchEncoder.getPosition()) <= 0.2) {
                setState(ClimberState.ArmBack);
                rotateClimbingHookDown();
            }
        } else if(state == ClimberState.ArmBack) {
            setState(ClimberState.ReachBarEnd);
            extendArm();
        } else if(state == ClimberState.ReachBarEnd) {
            if(Math.abs(Constants.MAX_ARM_DISTANCE - winchEncoder.getPosition()) <= 0.2) {
                setState(ClimberState.RetractHalfway);
                halfExtendArm();
            }
        } else if(state == ClimberState.RetractHalfway) {
            if((Math.abs(((Constants.MAX_ARM_DISTANCE - retractedPosition) * 0.5) - winchEncoder.getPosition()) <= 0.2)) {
                setState(ClimberState.ArmUpBarEnd);
                rotateClimbingHookVertical();
            }
        }
    }
}
