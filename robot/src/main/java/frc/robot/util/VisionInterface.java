package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionInterface {
    public static double getRelativeDistanceToHub() {
        return SmartDashboard.getNumber("RelativeDistanceToHub", -1.0d);
    }

    public static double getRelativeAngleToHub() {
        return SmartDashboard.getNumber("RelativeAngleToHub", Double.MAX_VALUE);
    }

    public static double getFlywheelVelocity() {
        return SmartDashboard.getNumber("Requested Flywheel RPM", 0.0d);
    }

    public static double getRequestedTurretAngle() {
        return SmartDashboard.getNumber("Requested Turret Angle", 0.0d);
    }

    public static double getRequestedHoodAngle() {
        return SmartDashboard.getNumber("Requested Hood Angle", 0.0d);
    }

    public static void selectCamera(int camera) {
        SmartDashboard.putNumber("CurrentCameraNumber", camera);
    }

    public static boolean canTrackHub() {
        return SmartDashboard.getBoolean("TargetFoundInFrame", false);
    }

    public static void debug() {
        SmartDashboard.putNumber("DEBUG DISTANCE", getRelativeDistanceToHub());
        SmartDashboard.putNumber("DEBUG ANGLE", getRelativeAngleToHub());
        SmartDashboard.putBoolean("DEBUG CANTRACK", canTrackHub());
    }

    private VisionInterface() {
        throw new IllegalStateException("VisionInterface cannot be instantiated!");
    }
}
