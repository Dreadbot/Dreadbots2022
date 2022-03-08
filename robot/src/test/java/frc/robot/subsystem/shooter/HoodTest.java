package frc.robot.subsystem.shooter;

import static org.junit.Assert.assertEquals;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DigitalInput;

public class HoodTest {
    public static final double DELTA = 1e-2;

    private CANSparkMax hoodMotor = new CANSparkMax(0, MotorType.kBrushless);
    private DigitalInput upperLimitSwitch = new DigitalInput(0);
    private DigitalInput lowerLimitSwitch = new DigitalInput(1);
    private Hood hood = new Hood(hoodMotor, lowerLimitSwitch, upperLimitSwitch);

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
    }

    @After
    public void cleanup() throws Exception {
        hood.close();
        hoodMotor.close();
    }
    
    @Test
    public void testDegreesToRotationsAngle() {
        double lowerRotations = -143;
        double lowerAngle = 82;
        double upperRotations = 60;
        double upperAngle = 60;

        hood.setLowerMotorLimit(lowerRotations);
        hood.setUpperMotorLimit(upperRotations);

        assertEquals(upperRotations, hood.convertDegreesToRotations(upperAngle), DELTA);
        assertEquals(lowerRotations, hood.convertDegreesToRotations(lowerAngle), DELTA);

        assertEquals(-78.04, hood.convertDegreesToRotations(74.96), DELTA);
        assertEquals(22.537, hood.convertDegreesToRotations(64.06), DELTA);
    }

    @Test
    public void testRotationsToDegrees() {
        double lowerRotations = -143;
        double lowerAngle = 82;
        double upperRotations = 60;
        double upperAngle = 60;

        hood.setLowerMotorLimit(lowerRotations);
        hood.setUpperMotorLimit(upperRotations);

        assertEquals(upperAngle, hood.convertRotationsToDegrees(upperRotations), DELTA);
        assertEquals(lowerAngle, hood.convertRotationsToDegrees(lowerRotations), DELTA);

        assertEquals(74.96, hood.convertRotationsToDegrees(-78.04), DELTA);
        assertEquals(64.06, hood.convertRotationsToDegrees(22.537), DELTA);
    }
}
