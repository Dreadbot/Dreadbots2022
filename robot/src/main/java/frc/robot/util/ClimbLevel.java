package frc.robot.util;

import frc.robot.Constants;

public enum ClimbLevel {
    MEDIUM(Constants.MEDIUM_CLIMBER_RANGE),
    HIGH(Constants.CLIMBER_RANGE);

    private double climbTarget;

    ClimbLevel(double climbTarget) {
        this.climbTarget = climbTarget;
    }

    public double getClimbTarget() {
        return climbTarget;
    }
}
