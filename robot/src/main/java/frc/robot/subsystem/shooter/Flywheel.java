package frc.robot.subsystem.shooter;

import javax.print.attribute.standard.RequestingUserName;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

public class Flywheel extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private final CANSparkMax motor;
    private SparkMaxPIDController pidController;
    private RelativeEncoder encoder;

    private double lastVelocity;

    public Flywheel(CANSparkMax motor) {
        this.motor = motor;

        if(!Constants.FLYWHEEL_ENABLED) {
            motor.close();
            
            return;
        }

        this.pidController = motor.getPIDController();
        this.encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kCoast);

        pidController.setP(Constants.FLYWHEEL_P_GAIN);
        pidController.setI(Constants.FLYWHEEL_I_GAIN);
        pidController.setD(Constants.FLYWHEEL_D_GAIN);
        pidController.setIZone(Constants.FLYWHEEL_I_ZONE);
        pidController.setFF(Constants.FLYWHEEL_FF_GAIN);
        pidController.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT);

        SmartDashboard.putNumber("Requested Flywheel RPM", 0.0d);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Flywheel Velocity", getVelocity());
    }

    public void setVelocity(double velocity) {
        if(!Constants.FLYWHEEL_ENABLED) return;

        if(velocity != lastVelocity) {
            // Prevents the motor from going beyond 5700RPM
            velocity = Math.min(velocity, Constants.FLYWHEEL_MAX_RPM);

            // Commands the motor to approach the requested angular speed.
            pidController.setReference(velocity, ControlType.kVelocity);
            
            lastVelocity = velocity;
        }
    }

    public void idle() {
        if(!Constants.FLYWHEEL_ENABLED) return;

        lastVelocity = 0.0d;
        
        motor.set(0.0d);
    }

    public double getVelocity() {
        if(!Constants.FLYWHEEL_ENABLED) return 0.0d;

        return encoder.getVelocity();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.FLYWHEEL_ENABLED) return;

        stopMotors();
        motor.close();
    }

    @Override
    public void stopMotors() {
        if(!Constants.FLYWHEEL_ENABLED) return;

        motor.stopMotor();
    }
}
