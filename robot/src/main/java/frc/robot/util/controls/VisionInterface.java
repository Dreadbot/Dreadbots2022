package frc.robot.util.controls;

import java.util.function.BooleanSupplier;

import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;

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
        return SmartDashboard.getNumber("RelativeDistanceToHub", -1.0d) - 6.0d;
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

    /**
     * Selects the camera through a request via Shuffleboard.
     *
     * @param camera The camera id
     */
    public static void selectCamera(int camera) {
        SmartDashboard.putNumber("CurrentCameraNumber", camera);
    }

    /**
     * Requests the vision tracking state from the Shuffleboard.
     *
     * @return The state of the vision tracking.
     */
    public static boolean canTrackHub() {
        return SmartDashboard.getNumber("IsTargetFoundInFrame", 0.0d) == 1.0d;
    }
    private VisionInterface() throws IllegalStateException {
        throw new IllegalStateException("VisionInterface is a utility class. It should not be instantiated.");
    }
}
