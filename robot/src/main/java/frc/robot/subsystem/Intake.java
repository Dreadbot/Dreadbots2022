// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import frc.robot.Constants;

/**
 * The intake is the mechanism that takes cargo from the ground into the feeder mechanism.
 */
public class Intake extends DreadbotSubsystem {
    private final CANSparkMax motor;

    public Intake(CANSparkMax motor) {
        this.motor = motor;

        // Immediately close motors if subsystem is disabled.
        if(!Constants.INTAKE_ENABLED) {
            disable();
            motor.close();

            return;
        }

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);

        // Motor is inverted because by default a positive value represents motion
        // that moves the ball out of the intake.
        motor.setInverted(true);
    }

    /**
     * Spins the motor to deliver the ground cargo to the feeder mechanism.
     */
    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;
        if(isDisabled()) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(1.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Spins the motor to move the ball out of the robot.
     */
    public void outtake() {
        if(!Constants.INTAKE_ENABLED) return;
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
        if(!Constants.INTAKE_ENABLED) return;
        if(isDisabled()) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isIntaking() {
        if(!Constants.INTAKE_ENABLED) return false;
        if(isDisabled()) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { disable(); }

        return output > 0.0d;
    }

    public boolean isOuttaking() {
        if(!Constants.INTAKE_ENABLED) return false;
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
        if(!Constants.INTAKE_ENABLED) return;
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
