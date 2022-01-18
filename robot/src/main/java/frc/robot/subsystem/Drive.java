// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

public class Drive extends Subsystem {
    private final CANSparkMax leftFrontMotor;
    private final CANSparkMax rightFrontMotor;
    private final CANSparkMax leftBackMotor;
    private final CANSparkMax rightBackMotor;

    public Drive(CANSparkMax leftFrontMotor, CANSparkMax rightFrontMotor, CANSparkMax leftBackMotor,
            CANSparkMax rightBackMotor) {
        super("Drive");

        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;
    }

    public void drivePolar() {
        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        // TODO remove, only needed for unit tests
        leftFrontMotor.set(1.0d);
        rightFrontMotor.set(1.0d);
        leftBackMotor.set(1.0d);
        rightBackMotor.set(1.0d);

        //TODO logic
    }

    @Override
    protected void stopMotors() {
        this.leftFrontMotor.stopMotor();
        this.rightFrontMotor.stopMotor();
        this.leftBackMotor.stopMotor();
        this.rightBackMotor.stopMotor();
    }

    @Override
    public void close() throws Exception {
        leftFrontMotor.close();
        rightFrontMotor.close();
        leftBackMotor.close();
        rightBackMotor.close();
    }

    public CANSparkMax getRightBackMotor() {
        return rightBackMotor;
    }

    public CANSparkMax getLeftBackMotor() {
        return leftBackMotor;
    }

    public CANSparkMax getRightFrontMotor() {
        return rightFrontMotor;
    }

    public CANSparkMax getLeftFrontMotor() {
        return leftFrontMotor;
    }
}
