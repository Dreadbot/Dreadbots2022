package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.DreadbotMotor;

/**
 * The flywheel is the mechanism that shoots the ball out of the robot.
 */
public class Flywheel extends DreadbotSubsystem {
    private DreadbotMotor motor;

    private SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0.36518, 0.24261, 0.099094); // * 2.5
    private PIDController controller = new PIDController(0.16677, 0.4, 0);

    private double setVelocity = 0.0d;

    /**
     * Disabled constructor
     */
    public Flywheel() {
        disable();
    }

    public Flywheel(DreadbotMotor motor) {
        this.motor = motor;

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);

        motor.setP(Constants.FLYWHEEL_P_GAIN);
        motor.setI(Constants.FLYWHEEL_I_GAIN);
        motor.setD(Constants.FLYWHEEL_D_GAIN);
        motor.setIZone(Constants.FLYWHEEL_I_ZONE);
        motor.setFF(Constants.FLYWHEEL_FF_GAIN);
        motor.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT);

        controller.enableContinuousInput(0.0, 1.0);
    }

    @Override
    public void periodic() { }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotFlywheel");
        builder.setActuator(true);
        builder.setSafeState(this::stopMotors);

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
    public void setVelocity(final double velocity) {
        this.setVelocity = velocity;
        if(isDisabled()) return;

        try {
            motor.setVoltage(feedforward.calculate(velocity, 2.0) + controller.calculate(getTangentialVelocity(), velocity));
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isAtSetVelocity() {
        return Math.abs(getTangentialVelocity() - setVelocity) <= 0.25d;
    }

    /**
     * Coasts the motor down to stop while the flywheel is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Commands the motor to coast down to stop.
        try {
            setVelocity(7.0);
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

    public void intake() {
        motor.set(-0.25);
    }

    public double getSetVelocity() {
        return setVelocity;
    }
}
