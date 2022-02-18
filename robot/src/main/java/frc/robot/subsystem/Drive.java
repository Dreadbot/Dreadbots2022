// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * The drive is the mechanism that moves the robot across the field. We are using a mecanum drive.
 */
public class Drive extends DreadbotSubsystem {
    private CANSparkMax leftFrontMotor;
    private CANSparkMax rightFrontMotor;
    private CANSparkMax leftBackMotor;
    private CANSparkMax rightBackMotor;

    private MecanumDrive mecanumDrive;

    /**
     * Disabled Constructor
     */
    public Drive() {
        disable();
    }

    public Drive(CANSparkMax leftFrontMotor, CANSparkMax rightFrontMotor, CANSparkMax leftBackMotor,
            CANSparkMax rightBackMotor) {
        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;

        leftFrontMotor.restoreFactoryDefaults();
        rightFrontMotor.restoreFactoryDefaults();
        leftBackMotor.restoreFactoryDefaults();
        rightBackMotor.restoreFactoryDefaults();

        // According to the docs, motors must be inverted before they are passed into the MecanumDrive utility.
        rightFrontMotor.setInverted(true);
        rightBackMotor.setInverted(true);

        this.mecanumDrive = new MecanumDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    }

    /**
     * Drive method for joystick inputs
     *
     * @param joystickForwardAxis The robot's speed along the Y axis [-1.0..1.0]. Right is positive.
     * @param joystickLateralAxis The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
     *     positive.
     */
    public void driveCartesian(double joystickForwardAxis, double joystickLateralAxis, double zRotation) {
        if(isDisabled()) return;

        try {
            mecanumDrive.driveCartesian(joystickForwardAxis, joystickLateralAxis, zRotation);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Drive method for magnitude, angle, and rotation
     *
     * <p>Angles are measured counter-clockwise from straight ahead. The speed at which the robot
     * drives (translation) is independent from its angle or rotation rate.
     *
     * @param magnitude The robot's speed at a given angle [-1.0..1.0]. Forward is positive.
     * @param angle The angle around the Z axis at which the robot drives in degrees [-180..180].
     * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
     *     positive.
     */
    public void drivePolar(double magnitude, double angle, double zRotation) {
        if(isDisabled()) return;

        try {
            mecanumDrive.drivePolar(magnitude, angle, zRotation);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Converts joystick inputs to a polar angle.
     *
     * @param forwardAxis The joystick's position along the Y axis [-1.0..1.0]
     * @param lateralAxis The joystick's position along the X axis [-1.0..1.0]
     *
     * @return The angle of the joystick input
     */
    public static double getAngleDegreesFromJoystick(double forwardAxis, double lateralAxis) {
        double angleInRadians = Math.atan2(-forwardAxis, lateralAxis);
        double angleInDegrees = angleInRadians * 180.0d / Math.PI;
        angleInDegrees -= 90.0d;
        return (angleInDegrees <= -180.0d) ? angleInDegrees + 360.0d : angleInDegrees;
    }

    @Override
    public void stopMotors() {
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
