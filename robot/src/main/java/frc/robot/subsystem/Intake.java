// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

/**
 * The intake is the mechanism that takes cargo from the ground into the feeder mechanism.
 */
public class Intake extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private final CANSparkMax motor;

    public Intake(CANSparkMax motor) {
        this.motor = motor;

        // Immediately close motors if subsystem is disabled.
        if(!Constants.INTAKE_ENABLED) {
            motor.close();
            return;
        }

        // Motor is inverted because by default a positive value represents motion
        // that moves the ball out of the intake.
        motor.setInverted(true);
    }

    /**
     * Spins the motor to deliver the ground cargo to the feeder mechanism.
     */
    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(1.0d);
        } catch (IllegalStateException ignored) { /* No stack trace */ }
    }

    /**
     * Spins the motor to move the ball out of the robot.
     */
    public void outtake() {
        if(!Constants.INTAKE_ENABLED) return;

        // Set the motor to a high negative speed.
        try {
            motor.set(-1.0d);
        } catch (IllegalStateException ignored) { /* No stack trace */ }
    }

    /**
     * Stops the motor while the intake is not required.
     */
    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { /* No stack trace */ }
    }

    public boolean isIntaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { /* No stack trace */ }

        return output > 0.0d;
    }

    public boolean isOuttaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { /* No stack trace */ }

        return output < 0.0d;
    }

    @Override
    public void close() {
        if(!Constants.INTAKE_ENABLED) return;

        // Stop motor before closure.
        stopMotors();

        try {
            motor.close();
        } catch (IllegalStateException ignored) { /* No stack trace */ }
    }

    @Override
    public void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        // Use the built-in motor stop method.
        try {
            motor.stopMotor();
        } catch (IllegalStateException ignored) { /* No stack trace */ }
    }
}
