package frc.robot.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CargoKinematicsTest {
    private static final double DELTA = 1e-2;

    public static final CargoKinematics.ArcHeightCalculator ARC_HEIGHT_CALCULATOR = (distance -> (0.5 * distance) + 2.5);
    public static final double INITIAL_HEIGHT = 0.2d;
    public static final double TARGET_HEIGHT = 2.64d;

    private CargoKinematics kinematics;

    @Before
    public void setup() {
        kinematics = new CargoKinematics(ARC_HEIGHT_CALCULATOR, INITIAL_HEIGHT, TARGET_HEIGHT);
    }
    
    @Test
    public void toBallVelocity() {
        double metersPerSecond = kinematics.toBallVelocity(1.3d);
        assertEquals(7.699d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallVelocity(3.55d);
        assertEquals(9.254d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallVelocity(5.95d);
        assertEquals(10.698d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallVelocity(9.2d);
        assertEquals(12.404d, metersPerSecond, DELTA);
    }

    @Test
    public void toBallAngle() {
        double metersPerSecond = kinematics.toBallAngle(1.3d);
        assertEquals(81.154d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallAngle(3.55d);
        assertEquals(75.068d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallAngle(5.95d);
        assertEquals(71.974d, metersPerSecond, DELTA);

        metersPerSecond = kinematics.toBallAngle(9.2d);
        assertEquals(69.718d, metersPerSecond, DELTA);
    }
}
