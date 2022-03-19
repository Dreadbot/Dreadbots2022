// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

/**
 * The intake is the mechanism that takes cargo from the ground into the feeder mechanism.
 */
public class Intake extends DreadbotSubsystem {
    private CANSparkMax motor;

    /**
     * Disabled Constructor
     */
    public Intake() {
        disable();
    }

    public Intake(CANSparkMax motor) {
        this.motor = motor;

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);
    }

    /**
     * Spins the motor to deliver the ground cargo to the feeder mechanism.  
     */
    public void intake() {
        intake(1.0d);
    }

    /**
     * Spins the motor to deliver the ground cargo to the feeder mechanism.
     * @param power
     */
    public void intake(double power) {
        if(isDisabled()) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(power);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Spins the motor to move the ball out of the robot.
     */
    public void outtake() {
        if(isDisabled()) return;

        // Set the motor to a high negative speed.
        try {
            motor.set(-1.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Stops the motor while the intake is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public boolean isIntaking() {
        if(isDisabled()) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { disable(); }

        return output > 0.0d;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public boolean isOuttaking() {
        if(isDisabled()) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { disable(); }

        return output < 0.0d;
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
