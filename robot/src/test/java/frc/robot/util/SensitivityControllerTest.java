package frc.robot.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SensitivityControllerTest {
    public static final double DELTA = 1e-2;

    private SensitivityController sensitivityController;

    @Before
    public void setup() {
        var builder = new SensitivityController.Builder(-40.0d, -40.0d);
        sensitivityController = builder.build();
    }

    @Test
    public void calculate() {
        assertEquals(0.0921d, sensitivityController.calculate(0.387d), DELTA);
        assertEquals(-0.463d, sensitivityController.calculate(-0.736d), DELTA);

        assertEquals(1.0d, sensitivityController.calculate(1.0d), DELTA);
        assertEquals(-1.0d, sensitivityController.calculate(-1.0d), DELTA);
        assertEquals(0.0d, sensitivityController.calculate(0.0d), DELTA);

        assertEquals(1.0d, sensitivityController.calculate(32.0d), DELTA);
        assertEquals(-1.0d, sensitivityController.calculate(-55.0d), DELTA);
    }

    @Test
    public void setPercentageSensitivity() {
        sensitivityController.setPositivePercentageSensitivity(43.0d);
        sensitivityController.setNegativePercentageSensitivity(98.0d);

        assertEquals(43.0d, sensitivityController.getPositivePercentageSensitivity(), DELTA);
        assertEquals(98.0d, sensitivityController.getNegativePercentageSensitivity(), DELTA);

        sensitivityController.setPositivePercentageSensitivity(145.0d);
        sensitivityController.setNegativePercentageSensitivity(-443.0d);

        assertEquals(100.0d, sensitivityController.getPositivePercentageSensitivity(), DELTA);
        assertEquals(-100.0d, sensitivityController.getNegativePercentageSensitivity(), DELTA);
    }

    @Test
    public void getPositivePercentageSensitivity() {
        sensitivityController.setPositivePercentageSensitivity(68.0d);
        sensitivityController.setNegativePercentageSensitivity(88.0d);

        assertEquals(68.0d, sensitivityController.getPositivePercentageSensitivity(), DELTA);
    }

    @Test
    public void getNegativePercentageSensitivity() {
        sensitivityController.setPositivePercentageSensitivity(8.0d);
        sensitivityController.setNegativePercentageSensitivity(87.0d);

        assertEquals(87.0d, sensitivityController.getNegativePercentageSensitivity(), DELTA);
    }
}
