package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.DreadbotMath;
import frc.robot.util.MotorSafeSystem;

public class Hood extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private final DigitalInput lowerSwitch;
    private final DigitalInput upperSwitch;
    private final CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;

    private double lowerMotorLimit;
    private double upperMotorLimit;

    public Hood (CANSparkMax motor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.motor = motor;
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;

        if(!Constants.HOOD_ENABLED) {
            lowerSwitch.close();
            upperSwitch.close();
            motor.close();

            return;
        }

        motor.setIdleMode(IdleMode.kBrake);
        encoder = motor.getEncoder();
        pidController = motor.getPIDController();
         
        pidController.setP(0.1); // Change numbers maybe
        pidController.setI(0); 
        pidController.setD(0);
        pidController.setIZone(0);
        pidController.setFF(0.000015);
        pidController.setOutputRange(-.5, .5);
    }

    @Override
    public void periodic() {
        if(!Constants.HOOD_ENABLED) return; 

        SmartDashboard.putBoolean("Hood Lower Limit Switch", getLowerLimitSwitch());
        SmartDashboard.putBoolean("Hood Upper Limit Switch", getUpperLimitSwitch());

        SmartDashboard.putNumber("Hood Angle", getAngle());
    }

    public void setAngle(double angle) {
        if(!Constants.HOOD_ENABLED) return; 

        angle = DreadbotMath.clampValue(angle, Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
        double rotations = convertDegreesToRotations(angle);

        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setPosition(double rotations) {
        if(!Constants.HOOD_ENABLED) return; 

        rotations = DreadbotMath.clampValue(rotations, lowerMotorLimit, upperMotorLimit);

        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setSpeed(double speed) {
        if(!Constants.HOOD_ENABLED) return;

        speed = DreadbotMath.clampValue(speed, -1.0, 1.0);
        motor.set(speed);
    }

    public boolean getLowerLimitSwitch() {
        if(!Constants.HOOD_ENABLED) return false;

        return !lowerSwitch.get();
    }

    public boolean getUpperLimitSwitch() {
        if(!Constants.HOOD_ENABLED) return false;

        return !upperSwitch.get();
    }

    public double getAngle() {
        if(!Constants.HOOD_ENABLED) return 0.0d;

        double rotations = encoder.getPosition();
        return convertRotationsToDegrees(rotations);
    }

    public double getPosition() {
        if(!Constants.HOOD_ENABLED) return 0.0d;

        return encoder.getPosition();
    }

    public void setUpperMotorLimit(double rotations) {
        SmartDashboard.putNumber("Hood Upper Limit", rotations);

        this.upperMotorLimit = rotations;
    }

    public void setLowerMotorLimit(double rotations) {
        SmartDashboard.putNumber("Hood Lower Limit", rotations);

        this.upperMotorLimit = rotations;
    }

    private double convertRotationsToDegrees(double rotations) {
        // Slope of the line describing the relationship
        double degreesPerRotation = (Constants.MAX_HOOD_ANGLE - Constants.MIN_HOOD_ANGLE) / (upperMotorLimit - lowerMotorLimit);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = rotations - lowerMotorLimit;
        angle *= degreesPerRotation;
        angle += Constants.MIN_HOOD_ANGLE;

        return angle;
    }

    private double convertDegreesToRotations(double degrees) {
        // Slope of the line describing the relationship
        double rotationsPerDegree =  (upperMotorLimit - lowerMotorLimit) / (Constants.MAX_HOOD_ANGLE - Constants.MIN_HOOD_ANGLE);

        // This is a computation of the point-slope form of the linear relationship.
        // more info and a visualization on https://www.desmos.com/calculator/dedxfbgiip
        double angle = degrees - Constants.MIN_HOOD_ANGLE;
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
        if(!Constants.HOOD_ENABLED) return;

        motor.stopMotor();
    }
}
