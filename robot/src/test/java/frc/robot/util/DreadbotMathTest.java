package frc.robot.util;

import frc.robot.util.math.DreadbotMath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("SpellCheckingInspection")
public class DreadbotMathTest {
    public static final double DELTA = 1e-99;

    private double bottomValueD;
    private double topValueD;
    private double resultValueD;

    private float bottomValueF;
    private float topValueF;
    private double resultValueF;
    
    private int bottomValueI;
    private int topValueI;
    private int resultValueI;

    private boolean result;

    @Before
    public void setup() {}

    @After
    public void shutdown() {
        bottomValueD = 0.0d;
        topValueD = 0.0d;
        resultValueD = 0.0d;

        bottomValueF = 0.0f;
        topValueF = 0.0f;
        resultValueF = 0.0f;

        bottomValueI = 0;
        topValueI = 0;
        resultValueI = 0;

        result = false;
    }

    @Test
    public void inRangeDoubleTestExclusive() {
        bottomValueD = -1.0d;
        topValueD = 1.0d;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(0.5d, bottomValueD, topValueD, false);
        assertTrue(result);
        result = DreadbotMath.inRange(-0.5d, bottomValueD, topValueD, false);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(2.0d, bottomValueD, topValueD, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-2.0d, bottomValueD, topValueD, false);
        assertFalse(result);

        // Bounds Tests (Exclusive implies bounds are not considered part of the range)
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-1.0d, bottomValueD, topValueD, false);
        assertFalse(result);
    }

    @Test
    public void inRangeFloatTestExclusive() {
        bottomValueF = -1.0f;
        topValueF = 1.0f;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(0.5f, bottomValueF, topValueF, false);
        assertTrue(result);
        result = DreadbotMath.inRange(-0.5f, bottomValueF, topValueF, false);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(2.0f, bottomValueF, topValueF, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-2.0f, bottomValueF, topValueF, false);
        assertFalse(result);

        // Bounds Tests (Exclusive implies bounds are not considered part of the range)
        result = DreadbotMath.inRange(1.0f, bottomValueF, topValueF, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-1.0f, bottomValueF, topValueF, false);
        assertFalse(result);
    }

    @Test
    public void inRangeIntegerTestExclusive() {
        bottomValueI = -2;
        topValueI = 2;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(1, bottomValueI, topValueI, false);
        assertTrue(result);
        result = DreadbotMath.inRange(-1, bottomValueI, topValueI, false);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(3, bottomValueI, topValueI, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-3, bottomValueI, topValueI, false);
        assertFalse(result);

        // Bounds Tests (Exclusive implies bounds are not considered part of the range)
        result = DreadbotMath.inRange(2, bottomValueI, topValueI, false);
        assertFalse(result);
        result = DreadbotMath.inRange(-2, bottomValueI, topValueI, false);
        assertFalse(result);
    }

    @Test
    public void inRangeDoubleTestInclusive() {
        bottomValueD = -1.0d;
        topValueD = 1.0d;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(0.5d, bottomValueD, topValueD, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-0.5d, bottomValueD, topValueD, true);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(2.0d, bottomValueD, topValueD, true);
        assertFalse(result);
        result = DreadbotMath.inRange(-2.0d, bottomValueD, topValueD, true);
        assertFalse(result);

        // Bounds Tests (Inclusive implies bounds are considered part of the range)
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-1.0d, bottomValueD, topValueD, true);
        assertTrue(result);
    }

    @Test
    public void inRangeFloatTestInclusive() {
        bottomValueF = -1.0f;
        topValueF = 1.0f;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(0.5f, bottomValueF, topValueF, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-0.5f, bottomValueF, topValueF, true);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(2.0f, bottomValueF, topValueF, true);
        assertFalse(result);
        result = DreadbotMath.inRange(-2.0f, bottomValueF, topValueF, true);
        assertFalse(result);

        // Bounds Tests (Inclusive implies bounds are considered part of the range)
        result = DreadbotMath.inRange(1.0f, bottomValueF, topValueF, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-1.0f, bottomValueF, topValueF, true);
        assertTrue(result);
    }

    @Test
    public void inRangeIntegerTestInclusive() {
        bottomValueI = -2;
        topValueI = 2;

        // Regular Function Test (Within Range)
        result = DreadbotMath.inRange(1, bottomValueI, topValueI, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-1, bottomValueI, topValueI, true);
        assertTrue(result);

        // Regular Function Test (Outside Range)
        result = DreadbotMath.inRange(3, bottomValueI, topValueI, true);
        assertFalse(result);
        result = DreadbotMath.inRange(-3, bottomValueI, topValueI, true);
        assertFalse(result);

        // Bounds Tests (Inclusive implies bounds are considered part of the range)
        result = DreadbotMath.inRange(2, bottomValueI, topValueI, true);
        assertTrue(result);
        result = DreadbotMath.inRange(-2, bottomValueI, topValueI, true);
        assertTrue(result);
    }

    @Test
    public void inRangeOverloadFunctionEquality() {
        bottomValueD = -1.0;
        topValueD = 1.0;

        // Equality Assertion
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD, true);
        assertTrue(result);
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD);
        assertTrue(result);

        // Inequality Assertion
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD, false);
        assertFalse(result);
        result = DreadbotMath.inRange(1.0d, bottomValueD, topValueD);
        assertTrue(result);
    }

    @Test
    public void clampValueDoubleTest() {
        bottomValueD = -1.0d;
        topValueD = 1.0d;

        // Regular Function Test (Within Range)
        resultValueD = DreadbotMath.clampValue(0.5d, bottomValueD, topValueD);
        assertEquals(0.5d, resultValueD, DELTA);
        resultValueD = DreadbotMath.clampValue(-0.5d, bottomValueD, topValueD);
        assertEquals(-0.5, resultValueD, DELTA);

        // Regular Function Test (Outside Range)
        resultValueD = DreadbotMath.clampValue(1.5d, bottomValueD, topValueD);
        assertEquals(topValueD, resultValueD, DELTA);
        resultValueD = DreadbotMath.clampValue(-1.5d, bottomValueD, topValueD);
        assertEquals(bottomValueD, resultValueD, DELTA);
    }

    @Test
    public void clampValueFloatTest() {
        bottomValueF = -1.0f;
        topValueF = 1.0f;

        // Regular Function Test (Within Range)
        resultValueF = DreadbotMath.clampValue(0.5f, bottomValueF, topValueF);
        assertEquals(0.5f, resultValueF, DELTA);
        resultValueF = DreadbotMath.clampValue(-0.5f, bottomValueF, topValueF);
        assertEquals(-0.5f, resultValueF, DELTA);

        // Regular Function Test (Outside Range)
        resultValueF = DreadbotMath.clampValue(1.5f, bottomValueF, topValueF);
        assertEquals(topValueF, resultValueF, DELTA);
        resultValueF = DreadbotMath.clampValue(-1.5f, bottomValueF, topValueF);
        assertEquals(bottomValueF, resultValueF, DELTA);
    }

    @Test
    public void clampValueIntegerTest() {
        bottomValueI = -2;
        topValueI = 2;

        // Regular Function Test (Within Range)
        resultValueI = DreadbotMath.clampValue(1, bottomValueI, topValueI);
        assertEquals(1, resultValueI, DELTA);
        resultValueI = DreadbotMath.clampValue(-1, bottomValueI, topValueI);
        assertEquals(-1, resultValueI, DELTA);

        // Regular Function Test (Outside Range)
        resultValueI = DreadbotMath.clampValue(3, bottomValueI, topValueI);
        assertEquals(topValueI, resultValueI, DELTA);
        resultValueI = DreadbotMath.clampValue(-3, bottomValueI, topValueI);
        assertEquals(bottomValueI, resultValueI, DELTA);
    }

    @Test
    public void applyDeadbandToValueDoubleTest() {
        bottomValueD = -1.0d;
        topValueD = 1.0d;

        // Regular Function Test (Within Range)
        resultValueD = DreadbotMath.applyDeadbandToValue(0.25d, bottomValueD, topValueD, 0.0d);
        assertEquals(0.0d, resultValueD, DELTA);

        // Regular Function Test (Outside Range)
        resultValueD = DreadbotMath.applyDeadbandToValue(-1.25d, bottomValueD, topValueD, 0.0d);
        assertEquals(-1.25d, resultValueD, DELTA);
    }

    @Test
    public void applyDeadbandToValueFloatTest() {
        bottomValueF = -1.0f;
        topValueF = 1.0f;

        // Regular Function Test (Within Range)
        resultValueF = DreadbotMath.applyDeadbandToValue(0.25f, bottomValueF, topValueF, 0.0f);
        assertEquals(0.0f, resultValueF, DELTA);

        // Regular Function Test (Outside Range)
        resultValueF = DreadbotMath.applyDeadbandToValue(-1.25f, bottomValueF, topValueF, 0.0f);
        assertEquals(-1.25f, resultValueF, DELTA);
    }

    @Test
    public void applyDeadbandToValueIntegerTest() {
        bottomValueI = -2;
        topValueI = 2;

        // Regular Function Test (Within Range)
        resultValueI = DreadbotMath.applyDeadbandToValue(1, bottomValueI, topValueI, 0);
        assertEquals(0, resultValueI, DELTA);

        // Regular Function Test (Outside Range)
        resultValueI = DreadbotMath.applyDeadbandToValue(-3, bottomValueI, topValueI, 0);
        assertEquals(-3, resultValueI, DELTA);
    }
}
