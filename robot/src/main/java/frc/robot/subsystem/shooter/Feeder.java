package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import frc.robot.subsystem.DreadbotSubsystem;

/**
 * The feeder is the mechanism that delivers the cargo from the intake mechanism to the shooter mechanism.
 */
public class Feeder extends DreadbotSubsystem {
    private CANSparkMax motor;

    /**
     * Disabled Constructor
     */
    public Feeder() {
        disable();
    }

    public Feeder(CANSparkMax motor) {
        this.motor = motor;

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kCoast);
        motor.setInverted(true);
    }

    /**
     * Spins the motor to deliver the cargo to the shooter mechanism.
     */
    public void feed() {
        if(isDisabled()) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(1.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Stops the motor while the feeder is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * @return Whether the motor is currently feeding a ball.
     */
    public boolean isFeeding() {
        if(isDisabled()) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { disable(); }

        return output > 0.0d;
    }

    @Override
    public void stopMotors() {
        if(isDisabled()) return;

        // Use the built-in motor stop method.
        try {
            motor.stopMotor();
        } catch (IllegalStateException ignored) { disable(); }
    }

    @Override
    public void close() {
        // Stop motor before closure.
        stopMotors();

        try {
            motor.close();
        } catch (IllegalStateException ignored) { disable(); }
    }
}
