package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVLibError;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;
import frc.robot.util.DreadbotMotor;

/**
 * The feeder is the mechanism that delivers the cargo from the intake mechanism to the shooter mechanism.
 */
public class Feeder extends DreadbotSubsystem {
    private final DreadbotMotor motor;

    public Feeder() {
        this(new DreadbotMotor(new CANSparkMax(Constants.FEEDER_MOTOR_PORT, CANSparkMaxLowLevel.MotorType.kBrushless), "Feeder"));
    }

    /**
     * This should only be called by unit tests to inject a mock motor object
     */
    protected Feeder(DreadbotMotor motor) {
        if (!Constants.FEEDER_ENABLED) {
            disable();
            return;
        }

        this.motor = motor;

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        motor.setInverted(true);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("feederPosition", motor.getPosition());
    }

    /**
     * Spins the motor to deliver the cargo to the shooter mechanism.
     */
    public void feed(double speed) {
        if(isDisabled()) return;

        // Set the motor to a high positive speed.
        try {
            motor.set(speed);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Spins the motor to deliver the cargo to the shooter mechanism.
     */
    public void feed() {
        this.feed(1.0d);
    }

    /**
     * Spins the motor to return a failed ball to the shooter staging area.
     */
    public void intake() {
        if(isDisabled()) return;

        // Spins the motor to a high negative speed.
        try {
            motor.set(-1.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    public double getFeederPosition() {
        if(isDisabled()) return 0.0d;

        return motor.getPosition();
    }

    /**
     * Sets the idle mode setting for the SPARK MAX.
     *
     * @param mode Idle mode (coast or brake).
     * @return {@link REVLibError#kOk} if successful
     */
    public REVLibError setIdleMode(IdleMode mode) {
        return motor.setIdleMode(mode);
    }

    public void outtake() {
        if(isDisabled()) return;

        try {
            motor.set(-0.25);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * Stops the motor while the feeder is not required.
     */
    public void idle() {
        if(isDisabled()) return;

        // Set the motor to zero movement.
        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) { disable(); }
    }

    /**
     * @return Whether the motor is currently feeding a ball.
     */
    public boolean isFeeding() {
        if(isDisabled()) return false;

        // Get the current commanded speed. If there is a failure,
        // the output is considered zero.
        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) { disable(); }

        return output > 0.0d;
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
}
