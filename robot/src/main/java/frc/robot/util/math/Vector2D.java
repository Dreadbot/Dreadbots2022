package frc.robot.util.math;

/**
 * Ordered pair of double numbers. Written with the intention of being used
 * as a traditional physics implementation of vectors.
 */
public class Vector2D {
    public double x1;
    public double x2;

    /**
     * Constructs the zero vector.
     */
    public Vector2D() {
        this.x1 = 0.0d;
        this.x2 = 0.0d;
    }

    /**
     * Constructs a vector.
     *
     * @param x1 First number in pair
     * @param x2 Second number in pair
     */
    public Vector2D(double x1, double x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    /**
     * Constructs a vector in polar form.
     *
     * @param angle Angle, in radians
     * @param norm The norm, or magnitude desired
     * @return Newly constructed Vector2D
     */
    public static Vector2D polar(double angle, double norm) {
        return new Vector2D(norm * Math.cos(angle), norm * Math.sin(angle));
    }

    /**
     * Copy constructor.
     *
     * @param original Vector to copy
     */
    Vector2D(Vector2D original) {
        System.out.println("vector copied");
        this.x1 = original.x1;
        this.x2 = original.x2;
    }

    /**
     * Calculates the norm (magnitude) of the vector
     *
     * @return Vector norm
     */
    public double norm() {
        return Math.sqrt(x1 * x1 + x2 * x2);
    }

    /**
     * Calculates the terminal angle (radians) of the vector
     *
     * @return Vector angle (radians)
     */
    public double terminalAngle() {
        return Math.atan2(x2, x1);
    }

    /**
     * Calculates the delta angle (radians) between this vector and another.
     *
     * @param other The comparison vector
     * @return The delta angle between the original (this) and comparison vectors.
     */
    public double angleBetween(Vector2D other) {
        // Rotate vector to become a scaled version of i-hat
        final double radians = -other.terminalAngle();
        Vector2D comparisonVector = rotate(radians);

        return comparisonVector.terminalAngle();
    }

    /**
     * Scales this vector by a constant.
     *
     * @param scalar The constant to scale the vector
     * @return A new vector
     */
    public Vector2D scale(double scalar) {
        x1 *= scalar;
        x2 *= scalar;

        return new Vector2D(x1, x2);
    }

    public Vector2D rotate(double radians) {
        // Effective logic of a rotation matrix
        double xn1 = x1 * Math.cos(radians) + x2 * -Math.sin(radians);
        double xn2 = x1 * Math.sin(radians) + x2 * Math.cos(radians);

        return new Vector2D(xn1, xn2);
    }
}
