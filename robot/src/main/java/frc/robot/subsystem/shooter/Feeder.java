package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

/**
 * The feeder is the mechanism that delivers the cargo from the intake mechanism to the shooter mechanism.
 */
public class Feeder extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private final CANSparkMax motor;

    public Feeder(CANSparkMax motor) {
        this.motor = motor;

        // Immediately close motors if subsystem is disabled.
        if(!Constants.FEEDER_ENABLED) {
            motor.close();

            return;
        }

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kCoast);
    }

    /**
     * Spins the motor to deliver the cargo to the shooter mechanism.
     */
    public void feed() {
        if(!Constants.FEEDER_ENABLED) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(1.0d);
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Stops the motor while the feeder is not required.
     */
    public void idle() {
        if(!Constants.FEEDER_ENABLED) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) {}
    }

    /**
     * @return Whether the motor is currently feeding a ball.
     */
    public boolean isFeeding() {
        if(!Constants.FEEDER_ENABLED) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) {}

        return output > 0.0d;
    }

    @Override
    public void close() {
        if(!Constants.FEEDER_ENABLED) return;

        // Stop motor before closure.
        stopMotors();

        try {
            motor.close();
        } catch (IllegalStateException ignored) {}
    }
    
    @Override
    public void stopMotors() {
        if(!Constants.FEEDER_ENABLED) return;

        // Use the built-in motor stop method.
        try {
            motor.stopMotor();
        } catch (IllegalStateException ignored) {}
    }
}
