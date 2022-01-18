// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

public class Intake implements AutoCloseable {
    private final CANSparkMax leftIntakeMotor;
    private final CANSparkMax rightIntakeMotor;

    public Intake(CANSparkMax leftIntakeMotor, CANSparkMax rightIntakeMotor) {
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
    }

    public void intake() {
        //TODO logic
    }

    @Override
    public void close() throws Exception {
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
