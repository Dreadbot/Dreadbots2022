package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.DreadbotMotor;
import frc.robot.util.math.DreadbotMath;

public class Turret extends DreadbotSubsystem {
    private DreadbotMotor motor;

    private DigitalInput lowerSwitch;
    private DigitalInput upperSwitch;
    private double lowerMotorLimit;
    private double upperMotorLimit;

    private double setAngle;

    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;

    /**
     * Disabled Constructor
     */
    public Turret() {
        disable();
    }

    public Turret(DreadbotMotor motor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;
        this.motor = motor;

        motor.setIdleMode(IdleMode.kBrake);
        motor.setInverted(true);

        // PID coefficients
        kP = 0.12;
        kI = 0.0001d;//3e-5;
        kD = 0.00001;
        kIz = 10.0d;
        kFF = 0.000015;
        kMaxOutput = 0.6;
        kMinOutput = -0.6;

        // set PID coefficients
        motor.setP(kP);
        motor.setI(kI);
        motor.setD(kD);
        motor.setIZone(kIz);
        motor.setFF(kFF);
        motor.setOutputRange(kMinOutput, kMaxOutput);

        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("Turret P Gain", kP);
        SmartDashboard.putNumber("Turret I Gain", kI);
        SmartDashboard.putNumber("Turret D Gain", kD);
        SmartDashboard.putNumber("Turret I Zone", kIz);
        SmartDashboard.putNumber("Turret Feed Forward", kFF);
        SmartDashboard.putNumber("Turret Max Output", kMaxOutput);
        SmartDashboard.putNumber("Turret Min Output", kMinOutput);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotTurret");
        builder.setActuator(true);
        builder.setSafeState(this::stopMotors);

        builder.addStringProperty(
            "Current Command",
            () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "none",
            null);

        builder.addBooleanProperty("IsAtAngle", this::isAtSetAngle, null);
    }

    @Override
    public void periodic() {
        if(isDisabled()) return;

        SmartDashboard.putBoolean("Turret Lower Limit Switch", getLowerLimitSwitch());
        SmartDashboard.putBoolean("Turret Upper Limit Switch", getUpperLimitSwitch());

        SmartDashboard.putNumber("Turret Angle", getAngle());
        SmartDashboard.putNumber("Turret Position", getPosition());

        SmartDashboard.putNumber("Turret Set Angle", setAngle);

        SmartDashboard.putNumber("Turret Range", upperMotorLimit - lowerMotorLimit);

        // read PID coefficients from SmartDashboard
        double p   = SmartDashboard.getNumber("Turret P Gain", 0);
        double i   = SmartDashboard.getNumber("Turret I Gain", 0);
        double d   = SmartDashboard.getNumber("Turret D Gain", 0);
        double iz  = SmartDashboard.getNumber("Turret I Zone", 0);
        double ff  = SmartDashboard.getNumber("Turret Feed Forward", 0);
        double max = SmartDashboard.getNumber("Turret Max Output", 0);
        double min = SmartDashboard.getNumber("Turret Min Output", 0);

        // if PID coefficients on SmartDashboard have changed, write new values to controller
        if((p != kP)) { motor.setP(p); kP = p; }
        if((i != kI)) { motor.setI(i); kI = i; }
        if((d != kD)) { motor.setD(d); kD = d; }
        if((iz != kIz)) { motor.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { motor.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) {
            motor.setOutputRange(min, max);
            kMinOutput = min; kMaxOutput = max;
        }
    }

    public void setAngle(double angle) {
        this.setAngle = angle;
        if(isDisabled()) return;

        angle = DreadbotMath.clampValue(angle, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
        double rotations = convertDegreesToRotations(angle);

        motor.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public boolean isAtSetAngle() {
        if(isDisabled()) return true;

        return Math.abs(getAngle() - setAngle) <= 1.0d;
    }

    public void setPosition(double rotations) {
        if(isDisabled()) return;

        rotations = DreadbotMath.clampValue(rotations, lowerMotorLimit, upperMotorLimit);
        
        motor.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setSpeed(double speed) {
        if(isDisabled()) return;

        speed = DreadbotMath.clampValue(speed, -1.0d, 1.0d);
        motor.set(speed);
    }

    public boolean getLowerLimitSwitch() {
        if(isDisabled()) return false;

        return !lowerSwitch.get();
    }

    public boolean getUpperLimitSwitch() {
        if(isDisabled()) return false;

        return !upperSwitch.get();
    }

    public double getAngle() {
        if(isDisabled()) return 0.0d;

        double rotations = motor.getPosition();
        return convertRotationsToDegrees(rotations);
    }

    public double getPosition() {
        if(isDisabled()) return 0.0d;

        return motor.getPosition();
    }

    public void setUpperMotorLimit(double rotations) {
        if(isDisabled()) return;

        SmartDashboard.putNumber("Turret Upper Limit", rotations);

        this.upperMotorLimit = rotations;
    }

    public void setLowerMotorLimit(double rotations) {
        if(isDisabled()) return;

        SmartDashboard.putNumber("Turret Lower Limit", rotations);

        this.lowerMotorLimit = rotations;
    }

    private double convertRotationsToDegrees(double rotations) {
        // Slope of the line describing the relationship
        double degreesPerRotation = (Constants.MAX_TURRET_ANGLE - Constants.MIN_TURRET_ANGLE) / (upperMotorLimit - lowerMotorLimit);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = rotations - lowerMotorLimit;
        angle *= degreesPerRotation;
        angle += Constants.MIN_TURRET_ANGLE;

        return angle;
    }

    private double convertDegreesToRotations(double degrees) {
        // Slope of the line describing the relationship
        double rotationsPerDegree =  (upperMotorLimit - lowerMotorLimit) / (Constants.MAX_TURRET_ANGLE - Constants.MIN_TURRET_ANGLE);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = degrees - Constants.MIN_TURRET_ANGLE;
        angle *= rotationsPerDegree;
        angle += lowerMotorLimit;

        return angle;
    }

    public double getSetAngle() {
        return setAngle;
    }

    @Override
    public void close() throws Exception {
        lowerSwitch.close();
        upperSwitch.close();
    }

    @Override
    public void stopMotors() {
        if(isDisabled()) return;

        motor.stopMotor();
    }
}