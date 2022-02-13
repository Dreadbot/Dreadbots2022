package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeederTest {
    private Feeder feeder;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        CANSparkMax feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless);

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
        
        assertTrue(feeder.isFeeding());
    }

    @Test
    public void idle() {
        feeder.idle();

        assertFalse(feeder.isFeeding());
    }

    @Test
    public void stopMotors() {
        feeder.stopMotors();

        assertFalse(feeder.isFeeding());
    }

    @Test
    public void isFeeding() {
        feeder.feed();
        assertTrue(feeder.isFeeding());

        feeder.idle();
        assertFalse(feeder.isFeeding());
    }

    @Test
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
