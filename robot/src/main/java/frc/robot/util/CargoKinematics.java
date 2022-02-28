package frc.robot.util;

import frc.robot.Constants;
import frc.robot.util.math.Vector2D;

/**
 * Calculations class for ball physics.
 *
 * This calculator uses standard, algebra-based projectile motion (parameterized with time)
 * kinematics to calculate required initial velocity properties for a launched cargo.
 */
public class CargoKinematics {
    /**
     * Definition of function signature for the anonymous calculation of
     * arc height, dependent on the current turret displacement from the hub.
     *
     * In practice, the argument passed into
     * {@link CargoKinematics#CargoKinematics(ArcHeightCalculator, double, double)}
     * would most practically be a lambda function expression.
     */
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

    /**
     * Gets the norm (magnitude) of the ball velocity required in order to make a cargo shot attempt.
     *
     * @param horizontalDistance Distance (meters) from the shooter to the center point of the hub
     * @return Required norm (magnitude) of velocity
     */
    public double getBallVelocityNorm(double horizontalDistance) {
        Vector2D ballVelocity = getBallVelocity(horizontalDistance);

        return ballVelocity.norm();
    }

    /**
     * Gets the direction angle (degrees of the initial velocity vector in order to make a cargo shot attempt
     *
     * @param horizontalDistance Distance (meters) from the shooter to the center point of the hub
     * @return Required direction angle (degrees) of velocity
     */
    public double getBallDirectionAngle(double horizontalDistance) {
        Vector2D ballVelocity = getBallVelocity(horizontalDistance);

        return ballVelocity.terminalAngle() * 180 / Math.PI;
    }

    /**
     * Gets the ball velocity required in order to make a cargo shot attempt
     *
     * @param horizontalDistance Distance (meters) from the shooter to the center point of the hub
     * @return Required ball velocity
     */
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

    /**
     * Calculates the delta time from ball release to capture by the hub.
     *
     * @param initialVerticalVelocityComponent The initial vertical velocity of the ball
     * @return Hub capture time
     */
    private double getTimeToScore(double initialVerticalVelocityComponent) {
        double changeInHeightToHub = 2 * Constants.GRAVITY * (initialHeight - targetHeight);
        double timeDeterminant = Math.pow(initialVerticalVelocityComponent, 2) - changeInHeightToHub;

        double timeToScore = -initialVerticalVelocityComponent;
        timeToScore -= Math.sqrt(timeDeterminant);

        return timeToScore / Constants.GRAVITY;
    }
}
