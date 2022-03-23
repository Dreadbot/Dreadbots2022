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
import frc.robot.util.DreadbotMath;

public class Turret extends DreadbotSubsystem {
    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;

    private DigitalInput lowerSwitch;
    private DigitalInput upperSwitch;
    private double lowerMotorLimit;
    private double upperMotorLimit;

    private double setAngle;

    /**
     * Disabled Constructor
     */
    public Turret() {
        disable();
    }

    public Turret(CANSparkMax motor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;
        this.motor = motor;
        this.encoder = motor.getEncoder();
        this.pidController = motor.getPIDController();

        motor.setIdleMode(IdleMode.kBrake);
        motor.setInverted(true);

        pidController.setP(0.04);
//        pidController.setI(1e-4);
        pidController.setI(1e-4);
        pidController.setD(0);
        pidController.setIZone(5);
        pidController.setFF(0.000015);
        pidController.setOutputRange(-.3, .3);
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

        SmartDashboard.putNumber("Turret Range", upperMotorLimit - lowerMotorLimit);
    }

    public void setAngle(double angle) {
        this.setAngle = angle;
        if(isDisabled()) return;

        angle = DreadbotMath.clampValue(angle, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
        double rotations = convertDegreesToRotations(angle);

        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public boolean isAtSetAngle() {
        if(isDisabled()) return true;

        return Math.abs(getAngle() - setAngle) <= 1.0d;
    }

    public void setPosition(double rotations) {
        if(isDisabled()) return;

        rotations = DreadbotMath.clampValue(rotations, lowerMotorLimit, upperMotorLimit);
        
        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
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

        double rotations = encoder.getPosition();
        return convertRotationsToDegrees(rotations);
    }

    public double getPosition() {
        if(isDisabled()) return 0.0d;

        return encoder.getPosition();
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