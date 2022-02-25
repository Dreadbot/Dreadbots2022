package frc.robot.util;

import frc.robot.Constants;

public class CargoKinematics {
    public interface ArcHeightCalculator {
        double getArcHeight(double horizontalDistance);
    }

    private final ArcHeightCalculator arcHeightCalculator;

    private final double initialHeight;
    private final double targetHeight;

    public CargoKinematics(ArcHeightCalculator arcHeightCalculator, double initialHeight, double targetHeight) {
        this.arcHeightCalculator = arcHeightCalculator;
        this.initialHeight = initialHeight;
        this.targetHeight = targetHeight;
    }

    public double toBallVelocity(double horizontalDistance) {
        double initialVerticalVelocityComponent = getInitialVerticalVelocity(horizontalDistance);
        double timeToScore = getTimeToScore(initialVerticalVelocityComponent);

        double initialHorizontalVelocityComponent = horizontalDistance / timeToScore;

        return Math.sqrt(Math.pow(initialVerticalVelocityComponent, 2)
            + Math.pow(initialHorizontalVelocityComponent, 2));
    }

    public double toBallAngle(double horizontalDistance) {
        double initialVerticalVelocityComponent = getInitialVerticalVelocity(horizontalDistance);
        double timeToScore = getTimeToScore(initialVerticalVelocityComponent);

        double initialHorizontalVelocityComponent = horizontalDistance / timeToScore;

        return Math.atan(initialVerticalVelocityComponent / initialHorizontalVelocityComponent) * 180.0d / Math.PI;
    }

    private double getInitialVerticalVelocity(double horizontalDistance) {
        return Math.sqrt(-2 * Constants.GRAVITY * (arcHeightCalculator.getArcHeight(horizontalDistance) - initialHeight));
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
