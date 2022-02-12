package frc.robot.subsystem.shooter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import frc.robot.Constants;

public class FeederTest {
    public static final double DELTA = 1e-2;

    private Feeder feeder;
    private CANSparkMax feederMotor;

    @Before
    public void setup() {
        feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR_PORT, MotorType.kBrushless);
        feeder = new Feeder(feederMotor);
    }

    @After
    public void shutdown() throws Exception {
        feeder.close();
    }

    @Test
    public void feedTest() {
        feeder.feed();
        
        if(!Constants.FEEDER_ENABLED) {
            assertFalse(feeder.isFeeding());
            return;
        }
        
        assertTrue(feeder.isFeeding());
    }

    @Test
    public void idleTest() {
        feeder.idle();
        
        if(!Constants.FEEDER_ENABLED) {
            assertFalse(feeder.isFeeding());
            return;
        }
        
        assertFalse(feeder.isFeeding());
    }

    @Test
    public void stopTest() {
        feeder.stopMotors();

        if(!Constants.FEEDER_ENABLED) {
            assertFalse(feeder.isFeeding());
            return;
        }

        assertFalse(feeder.isFeeding());
    }
}
