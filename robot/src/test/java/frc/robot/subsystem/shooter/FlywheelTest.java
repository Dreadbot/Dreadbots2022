package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import frc.robot.Robot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.assertEquals;

public class FlywheelTest {
    public static final double DELTA = 1e-2;

    private Flywheel flywheel;
    private CANSparkMax flywheelMotor;
    private RelativeEncoder flywheelEncoder;
    private SparkMaxPIDController flywheelPidController;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        // This logic notifies the programmer which systems are disabled in the Constants file.
        // The DreadbotSubsystem class will throw a warning while the log level is here.
        Robot.LOGGER.setLevel(Level.INFO);

        flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
        flywheelEncoder = flywheelMotor.getEncoder();


        flywheel = new Flywheel(flywheelMotor);

        // Set log level higher than warnings, so that tests do not log disabled warnings.
        Robot.LOGGER.setLevel(Level.SEVERE);
    }

    @After
    public void shutdown() {
        flywheel.close();

        // Return to the regular log level.
        Robot.LOGGER.setLevel(Level.INFO);
    }

    @Test
    public void idle() {
        flywheel.idle();

        if(flywheel.isEnabled()) {
            assertEquals(0.0, flywheelMotor.get(), DELTA);
        }
    }

    @Test
    public void stopMotors() {
        flywheel.stopMotors();

        if(flywheel.isEnabled()) {
            assertEquals(0.0, flywheelMotor.get(), DELTA);
        }
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test(expected = Test.None.class /* No exception should be thrown */)
    public void close() {
        flywheel.close();

        // Despite making calls to closed objects, these functions should not
        // throw an exception. This test case is another check to ensure calls
        // to closed motors do not crash the robot.
        flywheel.setVelocity(3000.0d);
        flywheel.getVelocity();
        flywheel.idle();
        flywheel.stopMotors();
        flywheel.close();
    }
}
