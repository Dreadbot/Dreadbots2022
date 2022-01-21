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
        double magnitude = Math.sqrt(Math.pow(joystickForwardAxis, 2) + Math.pow(joystickLateralAxis, 2));
        double angle = Drive.getAngleDegreesFromJoystick(joystickForwardAxis, joystickLateralAxis);
        // double angle = Math.atan(joystickLateralAxis/joystickForwardAxis);
        // angle *= 180/Math.PI;
        // if(joystickForwardAxis == 0.0d) {
        //     angle = -Math.signum(joystickLateralAxis) * 90.0d;
        // }
        // else if (joystickForwardAxis > 0) {
        //     angle -= Math.signum(joystickLateralAxis) * 180.0d;

        //     if(joystickLateralAxis == 0.0d) {
        //         angle = 180.0d;
        //     }
        // }

        mecanumDrive.drivePolar(magnitude, angle, zRotation);
    }

    public static double getAngleDegreesFromJoystick(double forwardAxis, double lateralAxis) {
        double angleInRadians = Math.atan2(-forwardAxis, lateralAxis);
        double angleInDegrees = angleInRadians * 180.0d / Math.PI;
        angleInDegrees -= 90.0d;
        return (angleInDegrees <= -180.0d) ? angleInDegrees + 360.0d : angleInDegrees;
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
