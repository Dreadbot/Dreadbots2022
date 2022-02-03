// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import frc.robot.Constants;

public class Intake extends Subsystem {
    private final CANSparkMax externalMotor;
    private final CANSparkMax internalMotor;

    public Intake(CANSparkMax externalMotor, CANSparkMax internalMotor) {
        super("Intake");
        
        this.externalMotor = externalMotor;
        this.internalMotor = internalMotor;

        if(!Constants.INTAKE_ENABLED) {
            externalMotor.close();
            internalMotor.close();
        }
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;
        
        if(!isEnabled()) {
            stopMotors();
            return;
        }

        externalMotor.set(1.0d);
        internalMotor.set(1.0d);
    }

    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        externalMotor.set(-1.0d);
        internalMotor.set(-1.0d);
    }

    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;

        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        externalMotor.set(0.0d);
        internalMotor.set(0.0d);
    }

    @Override
    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        externalMotor.stopMotor();
        internalMotor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        externalMotor.close();
        internalMotor.close();
    }

    public CANSparkMax getExternalMotor() {
        return externalMotor;
    }

    public CANSparkMax getInternalIntakeMotor() {
        return internalMotor;
    }
}
