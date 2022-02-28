package frc.robot.util;

import frc.robot.util.math.Vector2D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Vector2DTest {
    public static double DELTA = 1e-2;

    private Vector2D vector2D;

    @Test
    public void norm() {
        vector2D = new Vector2D(5.0d, 12.0d);
        double norm = vector2D.norm();

        assertEquals(13.0d, norm, DELTA);
    }

    @Test
    public void terminalAngle() {
        vector2D = new Vector2D(5.0d, 5.0d);
        double radians = vector2D.terminalAngle();

        assertEquals(Math.PI / 4.0d, radians, DELTA);
    }

    @Test
    public void angleBetween() {
        vector2D = Vector2D.polar(3.0d * Math.PI / 4.0d, 1.0d);
        Vector2D other = Vector2D.polar(Math.PI / 4.0d, 1.0d);

        double angleBetween = vector2D.angleBetween(other);
        assertEquals(Math.PI / 2.0d, angleBetween, DELTA);
    }

    @Test
    public void scale() {
        vector2D = new Vector2D(2.5d, 3.5d);

        vector2D.scale(2.0d);
        assertEquals(5.0d, vector2D.x1, DELTA);
        assertEquals(7.0d, vector2D.x2, DELTA);
    }

    @Test
    public void rotate() {
        vector2D = new Vector2D(1.0d, 5.0d);
        Vector2D other = vector2D.rotate(Math.PI / 2);

        assertEquals(-5.0d, other.x1, DELTA);
        assertEquals(1.0d, other.x2, DELTA);
    }
}
