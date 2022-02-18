// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import frc.robot.Constants;

public class Drive extends DreadbotSubsystem {
    private final CANSparkMax leftFrontMotor;
    private final CANSparkMax rightFrontMotor;
    private final CANSparkMax leftBackMotor;
    private final CANSparkMax rightBackMotor;

    private MecanumDrive mecanumDrive;

    public Drive(CANSparkMax leftFrontMotor, CANSparkMax rightFrontMotor, CANSparkMax leftBackMotor,
            CANSparkMax rightBackMotor) {
        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;
        
        // Prevent SparkMax crashes.
        if(!Constants.DRIVE_ENABLED) {
            disable();
            leftFrontMotor.close();
            rightFrontMotor.close();
            leftBackMotor.close();
            rightBackMotor.close();

            return;
        }

        leftFrontMotor.restoreFactoryDefaults();
        rightFrontMotor.restoreFactoryDefaults();
        leftBackMotor.restoreFactoryDefaults();
        rightBackMotor.restoreFactoryDefaults();
        
        // Invert right motors
        rightFrontMotor.setInverted(true);
        rightBackMotor.setInverted(true);

        // According to the docs, motors must be inverted before they are passed into the mecanumdrive utility.
        this.mecanumDrive = new MecanumDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    }

    public void driveCartesian(double joystickForwardAxis, double joystickLateralAxis, double zRotation) {
        if(!Constants.DRIVE_ENABLED) return;
        if(isDisabled()) return;

        try {
            mecanumDrive.driveCartesian(joystickForwardAxis, joystickLateralAxis, zRotation);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public void drivePolar(double magnitude, double angle, double zRotation) {
        if(!Constants.DRIVE_ENABLED) return;
        if(isDisabled()) return;

        try {
            mecanumDrive.drivePolar(magnitude, angle, zRotation);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public static double getAngleDegreesFromJoystick(double forwardAxis, double lateralAxis) {
        double angleInRadians = Math.atan2(-forwardAxis, lateralAxis);
        double angleInDegrees = angleInRadians * 180.0d / Math.PI;
        angleInDegrees -= 90.0d;
        return (angleInDegrees <= -180.0d) ? angleInDegrees + 360.0d : angleInDegrees;
    }

    @Override
    public void stopMotors() {
        if(!Constants.DRIVE_ENABLED) return;
        if(isDisabled()) return;

        try {
            mecanumDrive.stopMotor();
        } catch (IllegalStateException ignored) { disable(); }
    }

    @Override
    public void close() {
        // Stop motors before closure
        stopMotors();

        try {
            leftFrontMotor.close();
            rightFrontMotor.close();
            leftBackMotor.close();
            rightBackMotor.close();
        } catch (IllegalStateException ignored) { disable(); }
    }
}
