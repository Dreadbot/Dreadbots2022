package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

public class Feeder extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
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

        motor.set(1.0d);
    }

    public void idle() {
        if(!Constants.FEEDER_ENABLED) return;

        motor.set(0.0d);
    }

    public boolean isFeeding() {
        if(!Constants.FEEDER_ENABLED) return false;

        return motor.get() > 0.0d;
    }

    @Override
    public void close() {
        if(!Constants.FEEDER_ENABLED) return;

        stopMotors();
        motor.close();
    }
    
    @Override
    public void stopMotors() {
        if(!Constants.FEEDER_ENABLED) return;

        motor.stopMotor();
    }
}
