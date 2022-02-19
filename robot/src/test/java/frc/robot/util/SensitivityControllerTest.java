package frc.robot.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SensitivityControllerTest {
    public static final double DELTA = 1e-2;

    private SensitivityController sensitivityController;

    @Before
    public void setup() {
        sensitivityController = new SensitivityController();
    }

    @Test
    public void calculate() {
        assertEquals(0.0921d, sensitivityController.calculate(0.387d), DELTA);
        assertEquals(-0.463d, sensitivityController.calculate(-0.736d), DELTA);

        assertEquals(1.0d, sensitivityController.calculate(1.0d), DELTA);
        assertEquals(-1.0d, sensitivityController.calculate(-1.0d), DELTA);
        assertEquals(0.0d, sensitivityController.calculate(0.0d), DELTA);
    }

    @Test
    public void setPercentageSensitivity() {
        sensitivityController.setPercentageSensitivity(43d, 98d);

        assertEquals(43d, sensitivityController.getPositivePercentageSensitivity(), DELTA);
        assertEquals(98d, sensitivityController.getNegativePercentageSensitivity(), DELTA);
    }

    @Test
    public void getPositivePercentageSensitivity() {
        sensitivityController.setPercentageSensitivity(68d, 88d);

        assertEquals(68d, sensitivityController.getPositivePercentageSensitivity(), DELTA);
    }

    @Test
    public void getNegativePercentageSensitivity() {
        sensitivityController.setPercentageSensitivity(8d, 87d);

        assertEquals(87d, sensitivityController.getNegativePercentageSensitivity(), DELTA);
    }
}
