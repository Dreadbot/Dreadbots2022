package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Provides an API for accessing vision processing outputs from NetworkTables.
 */
public class VisionInterface {
    /**
     * Gets the vision calculated distance (in inches) from the vision calculations on the pi.
     * Returns -1.0d if the value isn't found on the NetworkTables.
     *
     * @return Calculated distance from the hub (inches)
     */
    public static double getRelativeDistanceToHub() {
        return SmartDashboard.getNumber("RelativeDistanceToHub", -1.0d);
    }

    /**
     * Gets the vision calculated relative angle (in degrees) from the vision calculations on the pi.
     * Returns Double.MAX_VALUE if the value isn't found on the NetworkTables.
     *
     * @return Calculated relative angle to the hub (degrees)
     */
    public static double getRelativeAngleToHub() {
        return SmartDashboard.getNumber("RelativeAngleToHub", Double.MAX_VALUE);
    }

    public static void selectCamera(int camera) {
        SmartDashboard.putNumber("CurrentCameraNumber", camera);
    }

    public static boolean canTrackHub() {
        return SmartDashboard.getBoolean("TargetFoundInFrame", false);
    }

    private VisionInterface() {
        throw new IllegalStateException("VisionInterface cannot be instantiated!");
    }
}
