package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.math.CargoKinematics;
import frc.robot.util.controls.VisionInterface;
import frc.robot.util.DreadbotMotor;

/**
 * The flywheel is the mechanism that shoots the ball out of the robot.
 */
public class Flywheel extends DreadbotSubsystem {
    public static final double RPM_TO_TANGENTIAL_CONVERSION = 4.655E-03;
    public static final double TANGENTIAL_TO_RPM_CONVERSION = 2.148E02;

    private CargoKinematics cargoKinematics;
    private DreadbotMotor motor;

    private PIDController controller = new PIDController(0.16677 * 2, 3, 0);

    private double setVelocity = 0.0d;

    private double acceleration;

    private double lastVelocity;

    /**
     * Disabled constructor
     */
    public Flywheel() {
        disable();
    }

    public Flywheel(DreadbotMotor motor) {
        this.motor = motor;

//        this.cargoKinematics = new CargoKinematics(s -> 0.8 * s + 1.21, 0.5715, 2.6416);
//        this.cargoKinematics = new CargoKinematics(s -> 0.2 * s + 2.6, 0.5715, 2.6416);
//        this.cargoKinematics = new CargoKinematics(s -> 13.5, 0.5715, 2.6416);
        this.cargoKinematics = new CargoKinematics(s -> 0.7607 * s + 5.5229, 0.5715, 2.6416);
//        this.cargoKinematics = new CargoKinematics(s -> 0.8 * s + 1, 0.5715, 2.6416);

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);

        motor.setP(Constants.FLYWHEEL_P_GAIN);
        motor.setI(Constants.FLYWHEEL_I_GAIN);
        motor.setD(Constants.FLYWHEEL_D_GAIN);
        motor.setIZone(Constants.FLYWHEEL_I_ZONE);
        motor.setFF(Constants.FLYWHEEL_FF_GAIN);
        motor.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT);

        controller.enableContinuousInput(0.0, 1.0);

        lastVelocity = getTangentialVelocity();
    }

    @Override
    public void periodic() {
        double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
        SmartDashboard.putNumber("VS DistanceToHubMeters", distanceToHub);
        double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);
        SmartDashboard.putNumber("VS FinalCommandVelocity", velocity);

        acceleration = (getTangentialVelocity() - lastVelocity) / 0.02d;
        SmartDashboard.putNumber("Flywheel Acceleration", acceleration);

        lastVelocity = getTangentialVelocity();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotFlywheel");
        builder.setActuator(true);
        builder.setSafeState(this::stopMotors);

        builder.addStringProperty(
            "Current Command",
            () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "none",
            null);

        builder.addBooleanProperty("IsAtSetVelocity", this::isAtSetVelocity, null);
        builder.addDoubleProperty("Velocity (tan)", this::getTangentialVelocity, null);
        builder.addDoubleProperty("Motor RPM", this::getMotorAngularVelocity, null);
        builder.addDoubleProperty("Set Velocity", this::getSetVelocity, null);

        builder.addDoubleProperty("controllerP", this.controller::getP, this.controller::setP);
        builder.addDoubleProperty("controllerI", this.controller::getI, this.controller::setI);
        builder.addDoubleProperty("controllerD", this.controller::getD, this.controller::setD);
    }

    /**
     * Spools up the motor to the desired velocity in rotations per minute (RPM)
     *
     * @param velocity the motor shaft angular velocity, in RPM
     */
    public void setVelocity(double velocity) {
        this.setVelocity = velocity;
        if(isDisabled()) return;

        try {
            motor.setReference(velocity * TANGENTIAL_TO_RPM_CONVERSION, CANSparkMax.ControlType.kVelocity);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isAtSetVelocity() {
        return getTangentialVelocity() >= setVelocity;
    }

    /**
     * Coasts the motor down to stop while the flywheel is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Commands the motor to coast down to stop.
        try {
            setVelocity(1.0);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Gets the current velocity of the flywheel motor, in rotations
     * per minute.
     *
     * @return the motor shaft angular velocity, in RPM
     */
    public double getTangentialVelocity() {
        if(isDisabled()) return 0.0d;

        // Get the current commanded velocity. If there is a failure,
        // the output is considered zero.
        double velocity = 0.0d;
        try {
            velocity = getMotorAngularVelocity() * RPM_TO_TANGENTIAL_CONVERSION;
        } catch (IllegalStateException ignored) { disable(); }

        return velocity;
    }

    public double getMotorAngularVelocity() {
        if(isDisabled()) return 0.0d;

        double rpm = 0.0d;
        try {
            rpm = motor.getVelocity();
        } catch(IllegalStateException ignored) { disable(); }

        return rpm;
    }

    @Override
    public void stopMotors() {
        if(isDisabled()) return;

        // Use the built-in motor stop method.
        try {
            motor.stopMotor();
        } catch (IllegalStateException ignored) { disable(); }
    }

    @Override
    public void close() {
        // Stop motor before closure.
        stopMotors();

        try {
            motor.close();
        } catch (IllegalStateException ignored) { disable(); }
    }

    public double getSetVelocity() {
        return setVelocity;
    }

    public CargoKinematics getCargoKinematics() {
        return cargoKinematics;
    }
}
