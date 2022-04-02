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

public class Hood extends DreadbotSubsystem {
    private DigitalInput lowerSwitch;
    private DigitalInput upperSwitch;
    private DreadbotMotor motor;

    private double lowerMotorLimit;
    private double upperMotorLimit;

    private double setAngle;

    /**
     * Disabled constructor
     */
    public Hood() {
        disable();
    }

    public Hood (DreadbotMotor motor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.motor = motor;
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;

        motor.setIdleMode(IdleMode.kCoast);
        motor.setInverted(false);
         
        motor.setP(0.1); // Change numbers maybe
        motor.setI(0); 
        motor.setD(0);
        motor.setIZone(0);
        motor.setFF(0.000015);
        motor.setOutputRange(-.5, .5);

        SmartDashboard.putNumber("Requested Hood Angle", 0.0d);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotHood");
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

        SmartDashboard.putBoolean("Hood Lower Limit Switch", getLowerLimitSwitch());
        SmartDashboard.putBoolean("Hood Upper Limit Switch", getUpperLimitSwitch());

        SmartDashboard.putNumber("Hood Angle", getAngle());

        SmartDashboard.putNumber("HOOD RANGE", upperMotorLimit - lowerMotorLimit);
    }

    public void setAngle(double angle) {
        this.setAngle = angle;
        if(isDisabled()) return;

        angle = DreadbotMath.clampValue(angle, Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
        double rotations = convertDegreesToRotations(angle);

        try {
            motor.setReference(rotations, CANSparkMax.ControlType.kPosition);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isAtSetAngle() {
        if(isDisabled()) return true;

        return Math.abs(getAngle() - setAngle) <= 1.0d;
    }

    public void setSpeed(double speed) {
        if(isDisabled()) return;

        speed = DreadbotMath.clampValue(speed, -1.0, 1.0);

        try {
            motor.set(speed);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean getLowerLimitSwitch() {
        if(isDisabled()) return false;

        boolean limitSwitchStatus = false;
        try {
            limitSwitchStatus = !lowerSwitch.get();
        } catch (IllegalStateException ignored) { disable(); }

        return limitSwitchStatus;
    }

    public boolean getUpperLimitSwitch() {
        if(isDisabled()) return false;

        boolean limitSwitchStatus = false;
        try {
            limitSwitchStatus = !upperSwitch.get();
        } catch (IllegalStateException ignored) { disable(); }

        return limitSwitchStatus;
    }

    public double getAngle() {
        if(isDisabled()) return 0.0d;

        double rotations = 0.0d;
        try {
            rotations = motor.getPosition();
        } catch (IllegalStateException ignored) { disable(); }

        return convertRotationsToDegrees(rotations);
    }

    public double getPosition() {
        if(isDisabled()) return 0.0d;

        double rotations = 0.0d;
        try {
            rotations = motor.getPosition();
        } catch (IllegalStateException ignored) { disable(); }

        return rotations;
    }

    public void setUpperMotorLimit(double rotations) {
        SmartDashboard.putNumber("Hood Upper Limit", rotations);

        this.upperMotorLimit = rotations;
    }

    public void setLowerMotorLimit(double rotations) {
        SmartDashboard.putNumber("Hood Lower Limit", rotations);

        this.lowerMotorLimit = rotations;
    }

    public double convertRotationsToDegrees(double rotations) {
        // Slope of the line describing the relationship
        double degreesPerRotation = (Constants.MIN_HOOD_ANGLE - Constants.MAX_HOOD_ANGLE) / (upperMotorLimit - lowerMotorLimit);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = rotations - lowerMotorLimit;
        angle *= degreesPerRotation;
        angle += Constants.MAX_HOOD_ANGLE;
        
        return angle;
    }

    public double convertDegreesToRotations(double degrees) {
        // Slope of the line describing the relationship
        double rotationsPerDegree =  (upperMotorLimit - lowerMotorLimit) / (Constants.MIN_HOOD_ANGLE - Constants.MAX_HOOD_ANGLE);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = degrees - Constants.MIN_HOOD_ANGLE;
        angle *= rotationsPerDegree;
        angle += upperMotorLimit;

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
