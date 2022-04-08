// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.util.DreadbotMotor;
import frc.robot.util.math.DreadbotMath;

import java.util.stream.Stream;

/**
 * The drive is the mechanism that moves the robot across the field. We are using a mecanum drive.
 */
public class Drive extends DreadbotSubsystem {
    // Motor Objects
    private DreadbotMotor leftFrontMotor;
    private DreadbotMotor rightFrontMotor;
    private DreadbotMotor leftBackMotor;
    private DreadbotMotor rightBackMotor;

    // NavX Gyroscope
    private AHRS gyroscope;

    // Target ChassisSpeeds commanded by teleop directions or
    private ChassisSpeeds targetChassisSpeeds;

    // MecanumDrive calculations classes
    private MecanumDrive mecanumDrive;
    private MecanumDriveOdometry odometry;

    /**
     * To understand why we negate (-) each of the values, look at the "right-hand rule" in the attached link.
     * X is longitudinal (forward/backward) direction, and Y is lateral (side/side) direction.
     * http://i.stack.imgur.com/0hxY1.png
     */
    private final MecanumDriveKinematics kinematics = new MecanumDriveKinematics(
        new Translation2d( Constants.WHEEL_LONGITUDINAL_DISPLACEMENT,  Constants.WHEEL_LATERAL_DISPLACEMENT), // Left Front
        new Translation2d( Constants.WHEEL_LONGITUDINAL_DISPLACEMENT, -Constants.WHEEL_LATERAL_DISPLACEMENT), // Right Front
        new Translation2d(-Constants.WHEEL_LONGITUDINAL_DISPLACEMENT,  Constants.WHEEL_LATERAL_DISPLACEMENT), // Left Back
        new Translation2d(-Constants.WHEEL_LONGITUDINAL_DISPLACEMENT, -Constants.WHEEL_LATERAL_DISPLACEMENT)  // Right Back
    );

    // Velocity-Based Control Feedforward
    private final SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(
        Constants.WHEEL_FEED_STATIC_FRICTION_GAIN, // (volts)
        Constants.WHEEL_FEED_VELOCITY_GAIN,        // (volt-seconds per meter)
        Constants.WHEEL_FEED_ACCELERATION_GAIN     // (volt--seconds-squared per meter)
    );

    // Velocity-Based Control PID Error Correction:
    // Input (measurement) is current velocity, output is voltage, setpoint is target velocity
    private final PIDController leftFrontVelocityPID  = new PIDController(Constants.WHEEL_CONTROLLER_P, 0, 0);
    private final PIDController rightFrontVelocityPID = new PIDController(Constants.WHEEL_CONTROLLER_P, 0, 0);
    private final PIDController leftBackVelocityPID   = new PIDController(Constants.WHEEL_CONTROLLER_P, 0, 0);
    private final PIDController rightBackVelocityPID  = new PIDController(Constants.WHEEL_CONTROLLER_P, 0, 0);

    // Trajectory Tracking Holonomic Drive Controllers
    private final TrapezoidProfile.Constraints rotationProfile =
        new TrapezoidProfile.Constraints(Units.degreesToRadians(360.0d), Units.degreesToRadians(180));
    private final HolonomicDriveController driveController = new HolonomicDriveController(
        // X Controller
        new PIDController(Constants.TRAJECTORY_ERROR_CONTROLLER_P, 0, 0),
        // Y Controller
        new PIDController(Constants.TRAJECTORY_ERROR_CONTROLLER_P, 0, 0),
        // Rotation Controller
        new ProfiledPIDController(1, 0, 0, rotationProfile)
    );

    private Field2d field2d;

    /**
     * Disabled Constructor
     */
    public Drive() {
        disable();
    }

    public Drive(DreadbotMotor leftFrontMotor, DreadbotMotor rightFrontMotor, DreadbotMotor leftBackMotor,
            DreadbotMotor rightBackMotor, AHRS gyroscope) {
        this.leftFrontMotor = leftFrontMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.leftBackMotor = leftBackMotor;
        this.rightBackMotor = rightBackMotor;

        this.gyroscope = gyroscope;

        this.targetChassisSpeeds = new ChassisSpeeds();

        this.field2d = new Field2d();

        // Fully-configure motors before passing them to MecanumDrive.
        configureMotors();
        resetEncoders();

        odometry = new MecanumDriveOdometry(kinematics, gyroscope.getRotation2d());

        this.mecanumDrive = new MecanumDrive(leftFrontMotor.getSparkMax(), leftBackMotor.getSparkMax(),
            rightFrontMotor.getSparkMax(), rightBackMotor.getSparkMax());

        mecanumDrive.setSafetyEnabled(false);

        SmartDashboard.putData("Field", field2d);
    }

    @Override
    public void periodic() {
        if(isDisabled()) return;

        odometry.update(gyroscope.getRotation2d(), getWheelSpeeds());
        field2d.setRobotPose(getPose());

        SmartDashboard.putNumber("GYRO PITCH", gyroscope.getPitch());
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotDrive");

        builder.setActuator(true);
        builder.setSafeState(this::stopMotors);
        builder.addDoubleProperty("leftFrontVelocity", leftFrontMotor::getVelocity, null);
        builder.addDoubleProperty("rightFrontVelocity", rightFrontMotor::getVelocity, null);
        builder.addDoubleProperty("leftBackVelocity", leftBackMotor::getVelocity, null);
        builder.addDoubleProperty("rightBackVelocity", rightBackMotor::getVelocity, null);

        builder.addDoubleProperty("chassisSpeedsX", () -> getChassisSpeeds().vxMetersPerSecond, null);
        builder.addDoubleProperty("chassisSpeedsY", () -> getChassisSpeeds().vyMetersPerSecond, null);
        builder.addDoubleProperty("chassisSpeedsOmega", () -> getChassisSpeeds().omegaRadiansPerSecond, null);

        builder.addDoubleProperty("targetChassisSpeedsX", () -> targetChassisSpeeds.vxMetersPerSecond, null);
        builder.addDoubleProperty("targetChassisSpeedsY", () -> targetChassisSpeeds.vyMetersPerSecond, null);
        builder.addDoubleProperty("targetChassisSpeedsOmega", () -> targetChassisSpeeds.omegaRadiansPerSecond, null);

        builder.addDoubleProperty("gyroYawRate", gyroscope::getRate, null);
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
            mecanumDrive.feed();
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Gets the current drivetrain speeds in terms of field-robot movement, via ChassisSpeeds.
     *
     * @return current ChassisSpeeds
     */
    public ChassisSpeeds getChassisSpeeds() {
        if(isDisabled()) return new ChassisSpeeds();

        return kinematics.toChassisSpeeds(getWheelSpeeds());
    }

    /**
     * Commands the drivetrain to travel at a certain ChassisSpeeds.
     *
     * @param chassisSpeeds The desired robot speeds
     */
    public void setChassisSpeeds(ChassisSpeeds chassisSpeeds) {
        if(isDisabled()) return;

        targetChassisSpeeds = chassisSpeeds;

        setWheelSpeeds(kinematics.toWheelSpeeds(chassisSpeeds));
    }

    /**
     * Get the wheel speeds via a MecanumDriveWheelSpeeds object.
     *
     * @return the MecanumDriveWheelSpeeds object (all 0 if disabled).
     */
    public MecanumDriveWheelSpeeds getWheelSpeeds() {
        if(isDisabled()) return new MecanumDriveWheelSpeeds();

        return new MecanumDriveWheelSpeeds(
            leftFrontMotor.getVelocity(),
            rightFrontMotor.getVelocity(),
            leftBackMotor.getVelocity(),
            rightBackMotor.getVelocity()
        );
    }

    /**
     * Sets the requested voltages for each of the drive motors.
     *
     * @param leftFrontVoltage Voltage feedforward for the left front motor.
     * @param rightFrontVoltage Voltage feedforward for the right front motor.
     * @param leftBackVoltage Voltage feedforward for the left back motor.
     * @param rightBackVoltage Voltage feedforward for the right back motor.
     */
    public void setWheelVoltages(double leftFrontVoltage, double rightFrontVoltage,
                                 double leftBackVoltage, double rightBackVoltage) {
        if(isDisabled()) return;

        leftFrontMotor.setVoltage(leftFrontVoltage);
        rightFrontMotor.setVoltage(rightFrontVoltage);
        leftBackMotor.setVoltage(leftBackVoltage);
        rightBackMotor.setVoltage(rightBackVoltage);
    }

    /**
     * Sets the requested velocities for each of the drive motors.
     *
     * @param wheelSpeeds The requested wheel velocities
     */
    public void setWheelSpeeds(MecanumDriveWheelSpeeds wheelSpeeds) {
        if(isDisabled()) return;

        leftFrontVelocityPID.setSetpoint(wheelSpeeds.frontLeftMetersPerSecond);
        rightFrontVelocityPID.setSetpoint(wheelSpeeds.frontRightMetersPerSecond);
        leftBackVelocityPID.setSetpoint(wheelSpeeds.rearLeftMetersPerSecond);
        rightBackVelocityPID.setSetpoint(wheelSpeeds.rearRightMetersPerSecond);

        leftFrontMotor.setVoltage(velocityToVoltage(leftFrontVelocityPID, leftFrontMotor.getVelocity()));
        rightFrontMotor.setVoltage(velocityToVoltage(rightFrontVelocityPID, rightFrontMotor.getVelocity()));
        leftBackMotor.setVoltage(velocityToVoltage(leftBackVelocityPID, leftBackMotor.getVelocity()));
        rightBackMotor.setVoltage(velocityToVoltage(rightBackVelocityPID, rightBackMotor.getVelocity()));

        mecanumDrive.feed();
    }

    private double velocityToVoltage(PIDController velocityController, double currentVelocity) {
        final double rawVoltage = feedforward.calculate(velocityController.getSetpoint())
            + velocityController.calculate(currentVelocity);

        return DreadbotMath.clampValue(rawVoltage, -12.0, 12.0);
    }

    /**
     * Gets the average position of the front two encoders.
     *
     * @return Average position of the front two encoders.
     */
    public double getFrontEncoderAvg() {
        if(isDisabled()) return 0.0d;

        RelativeEncoder frontRightEncoder = rightFrontMotor.getEncoder();
        RelativeEncoder frontLeftEncoder = leftFrontMotor.getEncoder();

        return (frontRightEncoder.getPosition() + frontLeftEncoder.getPosition()) / 2.0d;
    }

    /**
     * Resets the encoder positions.
     */
    public void resetEncoders() {
        if(isDisabled()) return;

        rightFrontMotor.resetEncoder();
        leftFrontMotor.resetEncoder();
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

    /**
     * Gets the current robot odometry pose.
     *
     * @return Robot pose
     */
    public Pose2d getPose() {
        return odometry.getPoseMeters();
    }

    /**
     * Gets the drive kinematics object.
     *
     * @return Drive kinematics object
     */
    public MecanumDriveKinematics getKinematics() {
        return kinematics;
    }

    /**
     * Gets the holonomic drive controller
     *
     * @return Holonomic drive controller
     */
    public HolonomicDriveController getDriveController() {
        return driveController;
    }

    /**
     * Resets the robot pose odometry.
     *
     * @param poseMeters The requested "zero" position of the odometry.
     */
    public void resetRobotPose(Pose2d poseMeters) {
        odometry.resetPosition(poseMeters, gyroscope.getRotation2d());
    }

    /**
     * Returns the gyroscope yaw.
     *
     * @return The gyroscope yaw
     */
    public double getYaw() {
        return gyroscope.getYaw();
    }

    private void configureMotors() {
        Stream.of(leftFrontMotor, rightFrontMotor, leftBackMotor, rightBackMotor).forEach(motor -> {
            motor.restoreFactoryDefaults();
            motor.setIdleMode(IdleMode.kBrake);
            motor.setPositionConversionFactor(Constants.WHEEL_ROTATIONS_TO_METERS);
            motor.setVelocityConversionFactor(Constants.WHEEL_RPM_TO_METERS_PER_SECOND);
        });

        leftFrontMotor.setInverted(true);
        leftBackMotor.setInverted(true);
        rightFrontMotor.setInverted(false);
        rightBackMotor.setInverted(false);
    }

    public AHRS getGyroscope() {
        return gyroscope;
    }
}
