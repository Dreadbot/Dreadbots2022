package frc.robot.subsystem.shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.DreadbotMath;
import frc.robot.util.MotorSafeSystem;

public class Turret extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private final DigitalInput lowerSwitch;
    private final DigitalInput upperSwitch;
    private final CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;

    private double lowerMotorLimit;
    private double upperMotorLimit;

    public Turret(CANSparkMax motor, DigitalInput lowerSwitch, DigitalInput upperSwitch) {
        this.lowerSwitch = lowerSwitch;
        this.upperSwitch = upperSwitch;
        this.motor = motor;

        if(!Constants.TURRET_ENABLED) {
            lowerSwitch.close();
            upperSwitch.close();
            motor.close();

            return;
        }

        motor.setIdleMode(IdleMode.kBrake);
        encoder = motor.getEncoder();
        pidController = motor.getPIDController();
        
        pidController.setP(0.1);
        pidController.setI(1e-4);
        pidController.setD(0);
        pidController.setIZone(2.85);
        pidController.setFF(0.000015);
        pidController.setOutputRange(-.3, .3);
    }

    @Override
    public void periodic() {
        if(!Constants.TURRET_ENABLED) return;

        SmartDashboard.putBoolean("Turret Lower Limit Switch", getLowerLimitSwitch());
        SmartDashboard.putBoolean("Turret Upper Limit Switch", getUpperLimitSwitch());

        SmartDashboard.putNumber("Turret Angle", getAngle());
    }

    public void setAngle(double angle) {
        if(!Constants.TURRET_ENABLED) return; 

        angle = DreadbotMath.clampValue(angle, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
        double rotations = convertDegreesToRotations(angle);

        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setPosition(double rotations) {
        if(!Constants.TURRET_ENABLED) return;

        rotations = DreadbotMath.clampValue(rotations, lowerMotorLimit, upperMotorLimit);
        
        pidController.setReference(rotations, CANSparkMax.ControlType.kPosition);
    }

    public void setSpeed(double speed) {
        if(!Constants.TURRET_ENABLED) return;

        speed = DreadbotMath.clampValue(speed, -1.0d, 1.0d);
        motor.set(speed);
    }

    public boolean getLowerLimitSwitch() {
        if(!Constants.TURRET_ENABLED) return false;

        return !lowerSwitch.get();
    }

    public boolean getUpperLimitSwitch() {
        if(!Constants.TURRET_ENABLED) return false;

        return !upperSwitch.get();
    }

    public double getAngle() {
        if(!Constants.TURRET_ENABLED) return 0.0d;

        double rotations = encoder.getPosition();
        return convertRotationsToDegrees(rotations);
    }

    public double getPosition() {
        if(!Constants.TURRET_ENABLED) return 0.0d;

        return encoder.getPosition();
    }

    public void setUpperMotorLimit(double rotations) {
        SmartDashboard.putNumber("Turret Upper Limit", rotations);

        this.upperMotorLimit = rotations;
    }

    public void setLowerMotorLimit(double rotations) {
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
        if(!Constants.TURRET_ENABLED) return; 

        motor.stopMotor();
    }
}