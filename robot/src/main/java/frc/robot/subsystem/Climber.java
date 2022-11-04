package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.util.ClimbLevel;
import frc.robot.util.DreadbotMotor;

public class Climber extends DreadbotSubsystem {
    private final Solenoid leftNeutralHookActuator;
    private final Solenoid climbingHookActuator;
    private final DigitalInput bottomLimitSwitch;
    private final DreadbotMotor winchMotor;

    private double retractedPosition;

    /**
     * Disabled Constructor
     */
    public Climber() {
        PneumaticHub pneumaticHub = new PneumaticHub(21);
        this.leftNeutralHookActuator = pneumaticHub.makeSolenoid(Constants.NEUTRAL_HOOK_SOLENOID_ID);
        this.climbingHookActuator = pneumaticHub.makeSolenoid(Constants.POWER_HOOK_SOLENOID_ID);
        this.winchMotor = new DreadbotMotor(new CANSparkMax(Constants.WINCH_MOTOR_PORT, CANSparkMaxLowLevel.MotorType.kBrushless), "Winch");
        this.bottomLimitSwitch = new DigitalInput(Constants.CLIMBER_LIMIT_SWITCH_ID);

        init();
    }

    /**
     * Parameterized constructor for unit tests to inject mocks
     */
    public Climber(Solenoid neutralHookActuator, Solenoid climbingHookActuator, DreadbotMotor winchMotor, DigitalInput bottomLimitSwitch) {
        if (!Constants.CLIMB_ENABLED) {
            disable();
            return;
        }
        this.leftNeutralHookActuator = neutralHookActuator;
        this.climbingHookActuator = climbingHookActuator;
        this.winchMotor = winchMotor;
        this.bottomLimitSwitch = bottomLimitSwitch;

        init();
    }

    public void init() {
        winchMotor.restoreFactoryDefaults();
        winchMotor.setIdleMode(IdleMode.kBrake);
        winchMotor.setInverted(true);
        leftNeutralHookActuator.set(false);

        winchMotor.setP(0.1);
        winchMotor.setI(0);
        winchMotor.setD(0);
        winchMotor.setIZone(0);
        winchMotor.setFF(0.000015);
        winchMotor.setOutputRange(-0.1, 0.1);

        winchMotor.setPosition(0.0d);
        this.retractedPosition = winchMotor.getPosition();

        SmartDashboard.putNumber("WinchPosition", retractedPosition);
    }

    @Override
    public void periodic() {
        if(isDisabled()) return;
        SmartDashboard.putBoolean("Climber Lower Limit", getBottomLimitSwitch());
        SmartDashboard.putNumber("WinchPosition", winchMotor.getPosition());
        SmartDashboard.putNumber("RetractedPosition", retractedPosition);
        SmartDashboard.putString("Current Command",
            getCurrentCommand() != null ? getCurrentCommand().getName() : "none");
    }

    public void zeroEncoderPosition() {
        if(isDisabled()) return;
        try {
            winchMotor.setPosition(0);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void rotateClimbingHookDown() {
        if(isDisabled()) return;

        try {
            climbingHookActuator.set(false);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void rotateClimbingHookVertical() {
        if(isDisabled()) return;

        try {
            climbingHookActuator.set(true);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void rotateNeutralHookVertical() {
        if(isDisabled()) return;

        try {
            leftNeutralHookActuator.set(true);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void rotateNeutralHookDown() {
        if(isDisabled()) return;

        try {
            leftNeutralHookActuator.set(false);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void setWinch(double factor) {
        if(isDisabled()) return;

        try {
            winchMotor.set(factor);
        } catch (IllegalStateException ignored) { disable(); }
    }
    
    public void retractArm() {
        if(isDisabled()) return;

        try {
            winchMotor.set(-0.8);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void extendArm() {
        if(isDisabled()) return;

        try {
            winchMotor.set(1.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void halfExtendArm() {
        if(isDisabled()) return;

        try {
            winchMotor.setReference((Constants.MAX_ARM_DISTANCE - retractedPosition) * 0.5, ControlType.kPosition);
        } catch (IllegalStateException ignored) { disable(); }
    }
    
    public void idle(){
        if(isDisabled()) return;

        try {
            winchMotor.stopMotor();
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean getBottomLimitSwitch() {
        if(isDisabled()) return false;
        
        boolean limitSwitchValue = false;
        try {
            limitSwitchValue = !bottomLimitSwitch.get();
        } catch (IllegalStateException ignored) { disable(); }
        
        return limitSwitchValue;
    }
    
    @Override
    public void stopMotors() {
        if(isDisabled()) return;

        try {
            winchMotor.stopMotor();
            winchMotor.stopMotor();
        } catch (IllegalStateException ignored) { disable(); }
    }

    @Override
    public void close() throws Exception {
        // Stop motors before closure.
        stopMotors();
        
        leftNeutralHookActuator.close();
        climbingHookActuator.close();

        try {
            winchMotor.close();
            winchMotor.close();
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isPowerArmExtended(ClimbLevel climbLevel) {
        return Math.abs(winchMotor.getPosition() - retractedPosition) >= climbLevel.getClimbTarget();
    }

    public double getWinchPosition() {
        return winchMotor.getPosition();
    }

    public void updateRetractedPosition() {
        winchMotor.setPosition(0.0d);
        this.retractedPosition = winchMotor.getPosition();
    }
}
