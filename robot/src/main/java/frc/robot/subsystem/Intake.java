// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import frc.robot.Constants;

public class Intake extends Subsystem {
    private final CANSparkMax externalIntakeMotor;
    private final CANSparkMax internalIntakeMotor;

    public Intake(CANSparkMax externalIntakeMotor, CANSparkMax internalIntakeMotor) {
        super("Intake");
        
        this.externalIntakeMotor = externalIntakeMotor;
        this.internalIntakeMotor = internalIntakeMotor;
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;
        
        if(!isEnabled()) {
            stopMotors();
            return;
        }

        externalIntakeMotor.set(1.0d);
        internalIntakeMotor.set(1.0d);
    }

    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        externalIntakeMotor.set(-1.0d);
        internalIntakeMotor.set(-1.0d);
    }

    @Override
    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        externalIntakeMotor.stopMotor();
        internalIntakeMotor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        externalIntakeMotor.close();
        internalIntakeMotor.close();
    }

    public CANSparkMax getExternalIntakeMotor() {
        return externalIntakeMotor;
    }

    public CANSparkMax getInternalIntakeMotor() {
        return internalIntakeMotor;
    }
}
