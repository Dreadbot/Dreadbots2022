// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import frc.robot.Constants;

public class Intake extends Subsystem {
    private final CANSparkMax motor;

    public Intake(CANSparkMax motor) {
        super("Intake");
        
        this.motor = motor;

        if(!Constants.INTAKE_ENABLED) {
            motor.close();
        }
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;
        
        if(!isEnabled()) {
            stopMotors();
            return;
        }

        motor.set(1.0d);
    }

    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        motor.set(-1.0d);
    }

    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        motor.set(0.0d);
    }

    @Override
    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        motor.close();
    }

    public CANSparkMax getMotor() {
        return motor;
    }
}
