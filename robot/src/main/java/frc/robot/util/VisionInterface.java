package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionInterface {
    public static double getRelativeDistanceToHub() {
        return 0.0d;
    }

    public static double getRelativeAngleToHub() {
        return 0.0d;
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
}
