package frc.robot.util.math;

public class Vector2D {
    private double x1;
    private double x2;

    public Vector2D() {
        this.x1 = 0.0d;
        this.x2 = 0.0d;
    }

    public Vector2D(double x1, double x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(x1, 2) + Math.pow(x2, 2));
    }

    public double terminalAngle() {
        return Math.atan2(x2, x1);
    }

    public double angleBetween(Vector2D other) {
        // Rotate vector to become a scaled version of i-hat
        final double radians = -other.terminalAngle();
        Vector2D comparisonVector = rotate(radians);

        return comparisonVector.terminalAngle();
    }

    public Vector2D scale(double scalar) {
        return new Vector2D(scalar * x1, scalar * x2);
    }

    public Vector2D rotate(double radians) {
        // Effective logic of a rotation matrix
        double xn1 = x1 * Math.cos(radians) + x2 * -Math.sin(radians);
        double xn2 = x1 * Math.sin(radians) + x2 * Math.cos(radians);

        return new Vector2D(xn1, xn2);
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }
}
