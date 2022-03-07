// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.util.DreadbotMath;

/**
 * The drive is the mechanism that moves the robot across the field. We are using a mecanum drive.
 */
public class Drive extends DreadbotSubsystem {
    public static final SimpleMotorFeedforward FEEDFORWARD =
        new SimpleMotorFeedforward(0.12119d, 0.024942d, 0.0058527d);

    public static final double DRIVE_KP = 0.034824;

    public static final double MAX_SPEED_METERS_PER_SECOND = 8.0d;
    public static final double MAX_ACCELERATION_METERS_PER_SECOND_SQUARED = 5.0;

    public static final double RAMSETE_B = 2.0d;
    public static final double RAMSETE_ZETA = 0.7d;

    public static final double wheelCircumferenceMeters = 0.4787;
    public static final double wheelRotationsPerMotorRotations = 14.0d / 70.0d;
    public static final double metersTraveledPerMotorRotations = wheelCircumferenceMeters * wheelRotationsPerMotorRotations;

    private static final TrapezoidProfile.Constraints MAX_ROTATION =
        new TrapezoidProfile.Constraints(Units.degreesToRadians(360.0d), Units.degreesToRadians(180));

    private CANSparkMax leftFrontMotor;
    private CANSparkMax rightFrontMotor;
    private CANSparkMax leftBackMotor;
    private CANSparkMax rightBackMotor;

    // Input is current velocity, output is voltage, setpoint is target velocity
    private PIDController leftFrontVelocityPID = new PIDController(3, 0, 0);
    private PIDController rightFrontVelocityPID = new PIDController(3, 0, 0);
    private PIDController leftBackVelocityPID = new PIDController(3, 0, 0);
    private PIDController rightBackVelocityPID = new PIDController(3, 0, 0);


    private AHRS gyroscope;

    private MecanumDrive mecanumDrive;

    private final HolonomicDriveController driveController =
        new HolonomicDriveController(
            // X Controller
            new PIDController(DRIVE_KP, 0, 0),
            // Y Controller
            new PIDController(DRIVE_KP, 0, 0),
            // Rotation Controller
            new ProfiledPIDController(1, 0, 0, MAX_ROTATION)
        );

    private final MecanumDriveKinematics kinematics = new MecanumDriveKinematics(
        new Translation2d(-0.4191d, 0.1905d),
        new Translation2d(0.4191d, 0.1905d),
        new Translation2d(-0.4191d, -0.1905d),
        new Translation2d(0.4191d, -0.1905d)
    );

    private MecanumDriveOdometry odometry;

    /**
     * Disabled Constructor
     */
    public Drive() {
        disable();
    }

    @Override
    public void periodic() {
        if(isDisabled()) return;
        odometry.update(gyroscope.getRotation2d(), getWheelSpeeds());
    }

    public Drive(CANSparkMax leftFrontMotor, CANSparkMax rightFrontMotor, CANSparkMax leftBackMotor,
            CANSparkMax rightBackMotor) {
        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;

        this.gyroscope = new AHRS(I2C.Port.kMXP);

        leftFrontMotor.restoreFactoryDefaults();
        rightFrontMotor.restoreFactoryDefaults();
        leftBackMotor.restoreFactoryDefaults();
        rightBackMotor.restoreFactoryDefaults();

        leftFrontMotor.setIdleMode(IdleMode.kBrake);
        rightFrontMotor.setIdleMode(IdleMode.kBrake);
        leftBackMotor.setIdleMode(IdleMode.kBrake);
        rightBackMotor.setIdleMode(IdleMode.kBrake);

        // According to the docs, motors must be inverted before they are passed into the MecanumDrive utility.
        rightFrontMotor.setInverted(true);
        rightBackMotor.setInverted(true);

        leftFrontMotor.getEncoder().setPositionConversionFactor(Drive.metersTraveledPerMotorRotations);
        rightFrontMotor.getEncoder().setPositionConversionFactor(Drive.metersTraveledPerMotorRotations);
        leftBackMotor.getEncoder().setPositionConversionFactor(Drive.metersTraveledPerMotorRotations);
        rightBackMotor.getEncoder().setPositionConversionFactor(Drive.metersTraveledPerMotorRotations);

        leftFrontMotor.getEncoder().setVelocityConversionFactor(Drive.wheelRotationsPerMotorRotations);
        rightFrontMotor.getEncoder().setVelocityConversionFactor(Drive.wheelRotationsPerMotorRotations);
        leftBackMotor.getEncoder().setVelocityConversionFactor(Drive.wheelRotationsPerMotorRotations);
        rightBackMotor.getEncoder().setVelocityConversionFactor(Drive.wheelRotationsPerMotorRotations);

        resetEncoders();
        odometry = new MecanumDriveOdometry(kinematics, gyroscope.getRotation2d());

        this.mecanumDrive = new MecanumDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    }

    public void printMotorVelocities(){
        SmartDashboard.putNumber("Left back velocity", leftBackMotor.getEncoder().getVelocity());
        SmartDashboard.putNumber("Left front velocity", leftFrontMotor.getEncoder().getVelocity());
        SmartDashboard.putNumber("Right back velocity", rightBackMotor.getEncoder().getVelocity());
        SmartDashboard.putNumber("Right Front velocity", rightFrontMotor.getEncoder().getVelocity());
        System.out.println(rightBackMotor.getBusVoltage());
        System.out.println(leftBackMotor.getBusVoltage());
        System.out.println(rightFrontMotor.getBusVoltage());
        System.out.println(leftFrontMotor.getBusVoltage());
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

    public ChassisSpeeds getChassisSpeeds() {
        return kinematics.toChassisSpeeds(getWheelSpeeds());
    }

    public void setChassisSpeeds(ChassisSpeeds chassisSpeeds) {
        final var wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);

        setWheelSpeeds(wheelSpeeds);
    }

    /**
     * Get the wheel speeds in the MecanumDriveWheelSpeeds form.
     *
     * @return the MecanumDriveWheelSpeeds object (0 if disabled).
     */
    public MecanumDriveWheelSpeeds getWheelSpeeds() {
        if(isDisabled()) return new MecanumDriveWheelSpeeds(0, 0, 0, 0);

        return new MecanumDriveWheelSpeeds(
            leftFrontMotor.getEncoder().getVelocity(),
            rightFrontMotor.getEncoder().getVelocity(),
            leftBackMotor.getEncoder().getVelocity(),
            rightBackMotor.getEncoder().getVelocity()
        );
    }

    public void setWheelVoltages(double leftFrontVoltage, double rightFrontVoltage,
                                 double leftBackVoltage, double rightBackVoltage) {
        leftFrontMotor.setVoltage(leftFrontVoltage);
        rightFrontMotor.setVoltage(rightFrontVoltage);
        leftBackMotor.set(leftBackVoltage);
        rightBackMotor.set(rightBackVoltage);
    }

    public void setWheelSpeeds(MecanumDriveWheelSpeeds wheelSpeeds) {
        leftFrontVelocityPID.setSetpoint(wheelSpeeds.frontLeftMetersPerSecond);
        rightFrontVelocityPID.setSetpoint(wheelSpeeds.frontRightMetersPerSecond);
        leftBackVelocityPID.setSetpoint(wheelSpeeds.rearLeftMetersPerSecond);
        rightBackVelocityPID.setSetpoint(wheelSpeeds.rearRightMetersPerSecond);

        leftFrontMotor.setVoltage(velocityToVoltage(leftFrontVelocityPID, leftFrontMotor.getEncoder().getVelocity()));
        rightFrontMotor.setVoltage(velocityToVoltage(rightFrontVelocityPID, rightFrontMotor.getEncoder().getVelocity()));
        leftBackMotor.setVoltage(velocityToVoltage(leftBackVelocityPID, leftBackMotor.getEncoder().getVelocity()));
        rightBackMotor.setVoltage(velocityToVoltage(rightBackVelocityPID, rightBackMotor.getEncoder().getVelocity()));
    }

    private double velocityToVoltage(PIDController velocityController, double currentVelocity) {
        final double rawVoltage = FEEDFORWARD.calculate(velocityController.getSetpoint())
            + velocityController.calculate(currentVelocity);

        return DreadbotMath.clampValue(rawVoltage, -12.0, 12.0);
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

    public void resetEncoders() {
        rightFrontMotor.getEncoder().setPosition(0.0d);
        leftFrontMotor.getEncoder().setPosition(0.0d);
        rightBackMotor.getEncoder().setPosition(0.0d);
        leftBackMotor.getEncoder().setPosition(0.0d);
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

    public double getFrontEncoderAvg(){
        RelativeEncoder frontRightEncoder = rightFrontMotor.getEncoder();
        RelativeEncoder frontLeftEncoder = leftFrontMotor.getEncoder();
        double getEncoderAvg = ((frontRightEncoder.getPosition() ) + frontLeftEncoder.getPosition())/2;
        SmartDashboard.putNumber("FrontRightEncoder", frontRightEncoder.getPosition());
        SmartDashboard.putNumber("FrontLeftEncoder", frontLeftEncoder.getPosition());
        SmartDashboard.putNumber("AvgEncoder", getEncoderAvg);
        return getEncoderAvg;
    }

    public void setIdleMode(IdleMode mode){
        leftBackMotor.setIdleMode(mode);
        leftFrontMotor.setIdleMode(mode);
        rightBackMotor.setIdleMode(mode);
        rightFrontMotor.setIdleMode(mode);
    }
}
