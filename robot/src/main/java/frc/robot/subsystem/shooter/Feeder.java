package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Feeder extends SubsystemBase {
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

    public CANSparkMax getMotor() {
        return motor;
    }
}
