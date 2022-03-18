package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.tuning.SparkMaxTuningUtility;

/**
 * The flywheel is the mechanism that shoots the ball out of the robot.
 */
public class Flywheel extends DreadbotSubsystem {
    private CANSparkMax motor;

    @SuppressWarnings("FieldMayBeFinal")
    private RelativeEncoder encoder;
    @SuppressWarnings("FieldMayBeFinal")
    private SparkMaxPIDController pidController;

    private SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0.46501, 0.26802 * 3, 0.032936);
    private PIDController controller = new PIDController(0.3, 0.05, 0);

    private double setVelocity;

//    private SparkMaxTuningUtility tuner;

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

        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kCoast);

        pidController.setP(Constants.FLYWHEEL_P_GAIN);
        pidController.setI(Constants.FLYWHEEL_I_GAIN);
        pidController.setD(Constants.FLYWHEEL_D_GAIN);
        pidController.setIZone(Constants.FLYWHEEL_I_ZONE);
        pidController.setFF(Constants.FLYWHEEL_FF_GAIN);
        pidController.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT);

        SmartDashboard.putNumber("Requested Flywheel RPM", 0.0d);
        SmartDashboard.putNumber("Target Flywheel Velocity", 0.0d);

//        this.tuner = new SparkMaxTuningUtility(motor);
//        SmartDashboard.putData("tuner", tuner);
    }

    @Override
    public void periodic() { }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("DreadbotFlywheel");
        builder.setActuator(true);
        builder.setSafeState(this::stopMotors);

        builder.addBooleanProperty("IsAtSetVelocity", this::isAtSetVelocity, null);
        builder.addDoubleProperty("Velocity (tan)", this::getVelocity, null);
        builder.addDoubleProperty("Motor RPM", this::getMotorAngularVelocity, null);

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

        // Prevents the motor from going beyond its maximum 5700RPM
//        final double finalVelocity = Math.min(velocity, Constants.FLYWHEEL_MAX_RPM);
//        SmartDashboard.putNumber("Target Flywheel Velocity", finalVelocity);
//
//        // Commands the motor to approach the requested angular speed.
//        try {
//            pidController.setReference(finalVelocity, ControlType.kVelocity);
//        } catch (IllegalStateException ignored) { disable(); }

        try {
            motor.setVoltage(feedforward.calculate(velocity, 1.0) + controller.calculate(getVelocity(), velocity));
        } catch (IllegalStateException ignored) { disable(); }
    }

    public boolean isAtSetVelocity() {
        return Math.abs(getVelocity() - setVelocity) <= 50.0d;
    }

    /**
     * Coasts the motor down to stop while the flywheel is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Commands the motor to coast down to stop.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Gets the current velocity of the flywheel motor, in rotations
     * per minute.
     *
     * @return the motor shaft angular velocity, in RPM
     */
    public double getVelocity() {
        if(isDisabled()) return 0.0d;

        // Get the current commanded velocity. If there is a failure,
        // the output is considered zero.
        double velocity = 0.0d;
        try {
            velocity = getMotorAngularVelocity() / (115.60 * 5);
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

//    public SparkMaxTuningUtility getTuner() {
//        return tuner;
//    }
}
