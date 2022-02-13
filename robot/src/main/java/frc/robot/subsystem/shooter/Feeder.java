package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

public class Feeder extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    public static final double FEEDER_SPEED = 1.0d;

    private final CANSparkMax motor;

    public Feeder(CANSparkMax motor) {
        this.motor = motor;

        if(!Constants.FEEDER_ENABLED) {
            motor.close();

            return;
        }

        motor.restoreFactoryDefaults();
    }

    public void feed() {
        if(!Constants.FEEDER_ENABLED) return;

        try {
            motor.set(FEEDER_SPEED);
        } catch (IllegalStateException ignored) {}
    }

    public void idle() {
        if(!Constants.FEEDER_ENABLED) return;

        try {
            motor.set(0.0d);
        } catch (IllegalStateException ignored) {}
    }

    public boolean isFeeding() {
        if(!Constants.FEEDER_ENABLED) return false;

        double output = 0.0d;
        try {
            output = motor.get();
        } catch (IllegalStateException ignored) {}

        return output > 0.0d;
    }

    @Override
    public void close() {
        if(!Constants.FEEDER_ENABLED) return;

        stopMotors();

        try {
            motor.close();
        } catch (IllegalStateException ignored) {}
    }
    
    @Override
    public void stopMotors() {
        if(!Constants.FEEDER_ENABLED) return;

        try {
            motor.stopMotor();
        } catch (IllegalStateException ignored) {}
    }
}
