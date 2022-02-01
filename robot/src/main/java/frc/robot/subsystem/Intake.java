// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import frc.robot.Constants;

public class Intake extends Subsystem {
    private final CANSparkMax leftIntakeMotor;
    private final CANSparkMax rightIntakeMotor;

    public Intake(CANSparkMax leftIntakeMotor, CANSparkMax rightIntakeMotor) {
        super("Intake");
        
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;
        
        if(!isEnabled()) {
            stopMotors();
            return;
        }

        leftIntakeMotor.set(1.0d);
        rightIntakeMotor.set(1.0d);
    }

    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        leftIntakeMotor.set(-1.0d);
        rightIntakeMotor.set(-1.0d);
    }

    @Override
    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        leftIntakeMotor.stopMotor();
        rightIntakeMotor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        leftIntakeMotor.close();
        rightIntakeMotor.close();
    }

    public CANSparkMax getLeftIntakeMotor() {
        return leftIntakeMotor;
    }

    public CANSparkMax getRightIntakeMotor() {
        return rightIntakeMotor;
    }
}
