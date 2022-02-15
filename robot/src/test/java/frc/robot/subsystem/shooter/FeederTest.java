package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;

import static org.junit.Assert.*;

public class FeederTest {
    public static final double DELTA = 1e-2;

    private Feeder feeder;
    private CANSparkMax feederMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless);
        feeder = new Feeder(feederMotor);
    }

    @After
    public void shutdown() {
        feeder.close();
    }

    @Test
    public void feed() {
        feeder.feed();
        
        if(!Constants.FEEDER_ENABLED) {
            assertFalse(feeder.isFeeding());
            return;
        }

        assertEquals(1.0, feederMotor.get(), DELTA);
    }

    @Test
    public void idle() {
        feeder.idle();

        assertEquals(0.0, feederMotor.get(), DELTA);
    }

    @Test
    public void stopMotors() {
        feeder.stopMotors();

        assertEquals(0.0, feederMotor.get(), DELTA);
    }

    @Test
    public void isFeeding() {
        feeder.feed();
        assertTrue(feeder.isFeeding());

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
