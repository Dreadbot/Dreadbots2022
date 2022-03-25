package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.CargoKinematics;
import frc.robot.util.VisionInterface;

/**
 * The flywheel is the mechanism that shoots the ball out of the robot.
 */
public class Flywheel extends DreadbotSubsystem {
    private CargoKinematics cargoKinematics;
    private CANSparkMax motor;

    @SuppressWarnings("FieldMayBeFinal")
    private RelativeEncoder encoder;
    @SuppressWarnings("FieldMayBeFinal")
    private SparkMaxPIDController pidController;

    private SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0.36518, 0.24261 * 1, 0.099094); // * 2.5
    private PIDController controller = new PIDController(0.16677 * 2, 3, 0);

    private double setVelocity = 0.0d;

    /**
     * Disabled constructor
     */
    public Flywheel() {
        disable();
    }

    public Flywheel(CANSparkMax motor) {
        this.motor = motor;

        this.encoder = motor.getEncoder();
        this.pidController = motor.getPIDController();

        this.cargoKinematics = new CargoKinematics(s -> 0.5667 * s + 1.21, 0.5715, 2.6416);

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);

        pidController.setP(Constants.FLYWHEEL_P_GAIN);
        pidController.setI(Constants.FLYWHEEL_I_GAIN);
        pidController.setD(Constants.FLYWHEEL_D_GAIN);
        pidController.setIZone(Constants.FLYWHEEL_I_ZONE);
        pidController.setFF(Constants.FLYWHEEL_FF_GAIN);
        pidController.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT);

        controller.enableContinuousInput(0.0, 1.0);
    }

    @Override
    public void periodic() {
        double distanceToHub = Units.inchesToMeters(VisionInterface.getRelativeDistanceToHub());
        SmartDashboard.putNumber("VS DistanceToHubMeters", distanceToHub);
        double velocity = cargoKinematics.getBallVelocityNorm(distanceToHub);
        SmartDashboard.putNumber("VS FinalCommandVelocity", velocity);
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
//        velocity += 2;
        this.setVelocity = velocity;
        if(isDisabled()) return;

        try {
//            motor.setVoltage(feedforward.calculate(velocity, 2.0) + controller.calculate(getTangentialVelocity(), velocity));
            motor.getPIDController().setReference(velocity * 120 / 0.279, CANSparkMax.ControlType.kVelocity);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isAtSetVelocity() {
        return getTangentialVelocity() >= setVelocity;

//        return Math.abs(getTangentialVelocity() - setVelocity) <= 0.15d;
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
            velocity = getMotorAngularVelocity() * 0.00932;
        } catch (IllegalStateException ignored) { disable(); }

        return velocity;
    }

    public double getMotorAngularVelocity() {
        if(isDisabled()) return 0.0d;

        double rpm = 0.0d;
        try {
            rpm = encoder.getVelocity();
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

    public void intake() {
        motor.set(-0.25);
    }

    public double getSetVelocity() {
        return setVelocity;
    }

    public CargoKinematics getCargoKinematics() {
        return cargoKinematics;
    }
}
