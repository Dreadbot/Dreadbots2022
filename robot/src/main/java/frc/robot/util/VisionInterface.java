package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class VisionInterface {
    public static double getRelativeDistanceToHub() {
        return 0.0d;
    }

    public static double getRelativeAngleToHub() {
        return 0.0d;
    }

    public static double getFlywheelVelocity(boolean correctBallColor) {
        if (correctBallColor) {
            return SmartDashboard.getNumber("Requested Flywheel RPM", 0.0d);
        } else {
            // Wrong Color, just eject the ball
            return 500;
        }
    }

    public static double getRequestedTurretAngle(boolean correctBallColor) {
        if(correctBallColor){
            return SmartDashboard.getNumber("Requested Turret Angle", 0.0d);
        } else {
            return SmartDashboard.getNumber("Requested Turret Angle", 0.0d) + 10.0;
        }
    }

    public static double getRequestedHoodAngle(boolean correctBallColor) {
        if(correctBallColor){
            return SmartDashboard.getNumber("Requested Hood Angle", 0.0d);
        } else {
            return Constants.MAX_HOOD_ANGLE - 10.0d;
        }
    }
}
