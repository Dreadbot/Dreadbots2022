// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Drive extends Subsystem {
    private final CANSparkMax leftFrontMotor;
    private final CANSparkMax rightFrontMotor;
    private final CANSparkMax leftBackMotor;
    private final CANSparkMax rightBackMotor;

    private final MecanumDrive mecanumDrive;

    public Drive(CANSparkMax leftFrontMotor, CANSparkMax rightFrontMotor, CANSparkMax leftBackMotor,
            CANSparkMax rightBackMotor) {
        super("Drive");

        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;
        
        // Invert right motors
        rightFrontMotor.setInverted(true);
        rightBackMotor.setInverted(true);
        
        // According to the docs, motors must be inverted before they are passed into the mecanumdrive utility.
        this.mecanumDrive = new MecanumDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    }

    public void drive(double joystickForwardAxis, double joystickLateralAxis, double zRotation) {
        if(!isEnabled()) {
            stopMotors();
            return;
        }
        
        // For polar drive, calculate the magnitude and angle that the MecanumDrive should drive at.
        @SuppressWarnings("unused")
        double magnitude = Math.sqrt(Math.pow(joystickForwardAxis, 2) + Math.pow(joystickLateralAxis, 2));
        @SuppressWarnings("unused")
        double angle;
        if(joystickLateralAxis == 0.0d) {
            angle = 90.0d * Math.signum(joystickForwardAxis);
        } else {
            double arctangent = Math.atan(joystickForwardAxis / joystickLateralAxis);
            arctangent *= (180.0d / Math.PI);
            if(Math.signum(joystickForwardAxis) > 0.0d && Math.signum(arctangent) > 0.0d) {
                angle = arctangent;
            } else if(Math.signum(joystickForwardAxis) < 0.0d && Math.signum(arctangent) > 0.0d) {
                angle = arctangent - 180.0d;
            }
        }
    }

    @Override
    protected void stopMotors() {
        leftFrontMotor.stopMotor();
        rightFrontMotor.stopMotor();
        leftBackMotor.stopMotor();
        rightBackMotor.stopMotor();

        mecanumDrive.stopMotor();
    }

    @Override
    public void close() throws Exception {
        leftFrontMotor.close();
        rightFrontMotor.close();
        leftBackMotor.close();
        rightBackMotor.close();

        mecanumDrive.close();
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
