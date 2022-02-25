package frc.robot.util;

import frc.robot.Constants;
import frc.robot.util.math.Vector2D;

/**
 * Calculations class for the ball physics to score
 */
public class CargoKinematics {
    public interface ArcHeightCalculator {
        double getArcHeight(double horizontalDistance);
    }

    private final ArcHeightCalculator arcHeightCalculator;

    private final double initialHeight;
    private final double targetHeight;

    /**
     * Initialize a new CargoKinematics calculator object.
     *
     * @param arcHeightCalculator
     * @param initialHeight
     * @param targetHeight
     */
    public CargoKinematics(ArcHeightCalculator arcHeightCalculator, double initialHeight, double targetHeight) {
        this.arcHeightCalculator = arcHeightCalculator;
        this.initialHeight = initialHeight;
        this.targetHeight = targetHeight;
    }

    public double toBallVelocity(double horizontalDistance) {
        Vector2D ballVelocity = getBallVelocity(horizontalDistance);

        return ballVelocity.magnitude();
    }

    public double toBallAngle(double horizontalDistance) {
        Vector2D ballVelocity = getBallVelocity(horizontalDistance);

        return ballVelocity.terminalAngle() * 180 / Math.PI;
    }

    public Vector2D getBallVelocity(double horizontalDistance) {
        double initialVerticalVelocityComponent = getInitialVerticalVelocity(horizontalDistance);
        double timeToScore = getTimeToScore(initialVerticalVelocityComponent);

        return new Vector2D(horizontalDistance / timeToScore, initialVerticalVelocityComponent);
    }

    /**
     * Calculates the initial vertical velocity component required for the ball to reach the arc height.
     *
     * @param horizontalDistance How far the edge of the flywheel is from the center of the hub
     * @return The initial vertical velocity component
     */
    private double getInitialVerticalVelocity(double horizontalDistance) {
        final double ballHeightRange = arcHeightCalculator.getArcHeight(horizontalDistance) - initialHeight;

        // This formula is derived from the basic Kinematics for Projectile Motion
        return Math.sqrt(2 * -Constants.GRAVITY * ballHeightRange);
    }

    private double getTimeToScore(double initialVerticalVelocityComponent) {
        double changeInHeightToHub = initialHeight - targetHeight;
        changeInHeightToHub *= 2 * Constants.GRAVITY;
        double timeDeterminant = Math.pow(initialVerticalVelocityComponent, 2) - changeInHeightToHub;

        double timeToScore = -initialVerticalVelocityComponent;
        timeToScore -= Math.sqrt(timeDeterminant);

        return timeToScore / Constants.GRAVITY;
    }
}
