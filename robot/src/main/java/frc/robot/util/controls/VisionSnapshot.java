package frc.robot.util.controls;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;

public class VisionSnapshot {
    private Pose2d robotPose;
    private Translation2d relativeHubPose;

    public VisionSnapshot(Pose2d robotPose, Translation2d relativeHubPose) {
        this.robotPose = robotPose;
        this.relativeHubPose = relativeHubPose;
    }

    public Translation2d approximateRelativeHubPosition(Pose2d odometricPosition) {
        Translation2d deltaPosition = odometricPosition.relativeTo(robotPose).getTranslation();

        return relativeHubPose.minus(deltaPosition);
    }
}
