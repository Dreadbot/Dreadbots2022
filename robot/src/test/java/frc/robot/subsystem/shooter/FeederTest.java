package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import frc.robot.Robot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;

import java.util.logging.Level;

import static org.junit.Assert.*;

public class FeederTest {
    public static final double DELTA = 1e-2;

    private Feeder feeder;
    private CANSparkMax feederMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        // This logic notifies the programmer which systems are disabled in the Constants file.
        // The DreadbotSubsystem class will throw a warning while the log level is here.
        Robot.LOGGER.setLevel(Level.INFO);

        feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless);
        feeder = new Feeder(feederMotor);

        // Set log level higher than warnings, so that tests do not log disabled warnings.
        Robot.LOGGER.setLevel(Level.SEVERE);
    }

    @After
    public void shutdown() {
        feeder.close();

        // Return to the regular log level.
        Robot.LOGGER.setLevel(Level.INFO);
    }

    @Test
    public void feed() {
        feeder.feed();

        if (feeder.isEnabled()) {
            assertEquals(1.0, feederMotor.get(), DELTA);
        }
    }

    @Test
    public void idle() {
        feeder.idle();

        if (feeder.isEnabled()) {
            assertEquals(0.0, feederMotor.get(), DELTA);
        }
    }

    @Test
    public void stopMotors() {
        feeder.stopMotors();

        if (feeder.isEnabled()) {
            assertEquals(0.0, feederMotor.get(), DELTA);
        }
    }

    @Test
    public void isFeeding() {
        feeder.feed();
        if(feeder.isEnabled()) {
            assertTrue(feeder.isFeeding());
        } else {
            assertFalse(feeder.isFeeding());
        }

        feeder.idle();
        assertFalse(feeder.isFeeding());
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test(expected = None.class /* No exception should be thrown */)
    public void close() {
        feeder.close();

        // Despite making calls to closed objects, these functions should not
        // throw an exception. This test case is another check to ensure calls
        // to closed motors do not crash the robot.
        feeder.feed();
        feeder.idle();
        feeder.stopMotors();
        feeder.isFeeding();
        feeder.close();
    }
}
