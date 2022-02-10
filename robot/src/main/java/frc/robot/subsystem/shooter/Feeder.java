package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Feeder extends SubsystemBase {
    private final CANSparkMax motor;

    public Feeder(CANSparkMax motor) {
        this.motor = motor;
    }

    public void feed() {
        motor.set(1.0d);
    }

    public void idle() {
        motor.set(0.0d);
    }
}
