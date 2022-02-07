// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
    private final CANSparkMax motor;

    public Intake(CANSparkMax motor) {
        this.motor = motor;

        if(!Constants.INTAKE_ENABLED) {
            motor.close();

            return;
        }

        motor.setInverted(true);
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.set(1.0d);
    }

    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.set(-1.0d);
    }

    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;
        
        motor.set(0.0d);
    }

    public boolean isIntaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        return motor.get() > 0.0d;
    }

    public boolean isOuttaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        return motor.get() < 0.0d;
    }

    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.stopMotor();
    }

    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        motor.close();
    }

    public CANSparkMax getMotor() {
        return motor;
    }
}
