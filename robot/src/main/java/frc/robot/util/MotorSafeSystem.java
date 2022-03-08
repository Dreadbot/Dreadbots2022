package frc.robot.util;

/**
 * An object that manages hardware resources (such as {@link com.revrobotics.CANSparkMax}).
 * Any hardware managing object should have a method to completely stop mechanical functions.
 */
public interface MotorSafeSystem {
    /**
     * Stops all hardware functions handled by this class.
     */
    void stopMotors();
}
