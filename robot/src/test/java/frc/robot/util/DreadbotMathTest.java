package frc.robot.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DreadbotMathTest {
    public static final double DELTA = 1e-99;

    private double bottomValueD;
    private double topValueD;
    private float bottomValueF;
    private float topValueF;
    private int bottomValueI;
    private int topValueI;

    private boolean result;

    @Before
    public void setup() {}

    @After
    public void shutdown() throws Exception {
        bottomValueD = 0.0d;
        topValueD = 0.0d;
        bottomValueF = 0.0f;
        topValueF = 0.0f;
        bottomValueI = 0;
        topValueI = 0;

        result = false;
    }

    @Test
    public void inRangeDoubleTestExclusive() {
        bottomValueD = -1.0d;
        topValueD = 1.0d;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(0.5d, bottomValueD, topValueD, false);
        assertEquals(true, result);
        result = DreadbotMath.inRange(-0.5d, bottomValueD, topValueD, false);
        assertEquals(true, result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(2.0d, bottomValueD, topValueD, false);
        assertEquals(false, result);
        result = DreadbotMath.inRange(-2.0d, bottomValueD, topValueD, false);
        assertEquals(false, result);

        // Bounds Tests (Exclusive implies bounds are not considered part of the range)
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD, false);
        assertEquals(false, result);
        result = DreadbotMath.inRange(-1.0d, bottomValueD, topValueD, false);
        assertEquals(false, result);
    }
}
