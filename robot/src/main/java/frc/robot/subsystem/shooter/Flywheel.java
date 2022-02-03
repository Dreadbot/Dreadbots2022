package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.filter.SlewRateLimiter;
import frc.robot.Constants;
import frc.robot.subsystem.Subsystem;

public class Flywheel extends Subsystem {
    private final CANSparkMax motor;
    private SparkMaxPIDController pidController;
    private RelativeEncoder encoder;

    public double lastVelocity;

    public Flywheel(CANSparkMax motor) {
        super("Flywheel");

        this.motor = motor;

        if(!Constants.SHOOTER_ENABLED) {
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
    }

    public void ramp(double velocity) {
        if(!Constants.SHOOTER_ENABLED) return;

        if(velocity != lastVelocity) {
            // Prevents the motor from going beyond 5700RPM
            velocity = Math.min(velocity, Constants.FLYWHEEL_MAX_RPM);

            // Commands the motor to approach the requested angular speed.
            pidController.setReference(velocity, ControlType.kVelocity);
            
            lastVelocity = velocity;
        }
    }

    public void idle() {
        if(!Constants.SHOOTER_ENABLED) return;

        lastVelocity = 0.0d;
        
        motor.set(0.0d);
    }

    @Override
    public void close() throws Exception {
        if(!Constants.SHOOTER_ENABLED) return;

        motor.close();
    }

    @Override
    protected void stopMotors() {
        if(!Constants.SHOOTER_ENABLED) return;

        motor.stopMotor();
    }

    public double getVelocity() {
        if(!Constants.SHOOTER_ENABLED) return 0.0d;

        return encoder.getVelocity();
    }
}
