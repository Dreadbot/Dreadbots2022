package frc.robot.command.autonomous;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DreadbotMecanumDrive;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrajectoryAuton extends CommandBase {
    private final Timer m_timer = new Timer();
    private final PathPlannerTrajectory m_trajectory;
    private final Supplier<Pose2d> m_pose;
    private final MecanumDriveKinematics m_kinematics;
    private final HolonomicDriveController m_controller;
    private final double m_maxWheelVelocityMetersPerSecond;
    private final Consumer<MecanumDriveWheelSpeeds> m_outputWheelSpeeds;
    private final DreadbotMecanumDrive drive;

    private boolean reversed;

    public TrajectoryAuton(DreadbotMecanumDrive drive, PathPlannerTrajectory trajectory, double maxWheelVelocityMetersPerSecond) {
        this.drive = drive;
        this.m_trajectory = trajectory;
        this.m_maxWheelVelocityMetersPerSecond = maxWheelVelocityMetersPerSecond;

        this.m_pose = drive::getPose;
        this.m_kinematics = drive.getKinematics();
        this.m_controller = drive.getDriveController();
        this.m_outputWheelSpeeds = drive::setWheelSpeeds;

        SmartDashboard.putNumber("CurrentTrajectoryTime", 0);

        this.reversed = false;

        addRequirements(drive);
    }

    public TrajectoryAuton(DreadbotMecanumDrive drive, PathPlannerTrajectory trajectory, double maxWheelVelocityMetersPerSecond, boolean reversed) {
        this.drive = drive;
        this.m_trajectory = trajectory;
        this.m_maxWheelVelocityMetersPerSecond = maxWheelVelocityMetersPerSecond;

        this.m_pose = drive::getPose;
        this.m_kinematics = drive.getKinematics();
        this.m_controller = drive.getDriveController();
        this.m_outputWheelSpeeds = drive::setWheelSpeeds;

        SmartDashboard.putNumber("CurrentTrajectoryTime", 0);

        this.reversed = reversed;

        addRequirements(drive);
    }

    @Override
    public void initialize() {
        drive.resetRobotPose(m_trajectory.sample(0.0).poseMeters);

        m_timer.reset();
        m_timer.start();
    }

    @Override
    @SuppressWarnings("LocalVariableName")
    public void execute() {
        double curTime = m_timer.get();
        SmartDashboard.putNumber("CurrentTrajectoryTime", curTime);
        var desiredState = (PathPlannerTrajectory.PathPlannerState) m_trajectory.sample(curTime);

        var targetChassisSpeeds =
            m_controller.calculate(m_pose.get(), desiredState, desiredState.holonomicRotation);
        targetChassisSpeeds.omegaRadiansPerSecond *= .55;
        targetChassisSpeeds.vyMetersPerSecond *= .55;
        targetChassisSpeeds.vxMetersPerSecond *= .55;

        if(reversed) {
            targetChassisSpeeds.vxMetersPerSecond *= -1;
            targetChassisSpeeds.vyMetersPerSecond *= -1;
            targetChassisSpeeds.omegaRadiansPerSecond *= -1;
        }

        var targetWheelSpeeds = m_kinematics.toWheelSpeeds(targetChassisSpeeds);

        targetWheelSpeeds.desaturate(m_maxWheelVelocityMetersPerSecond);

        m_outputWheelSpeeds.accept(targetWheelSpeeds);
    }

    @Override
    public void end(boolean interrupted) {
        m_timer.stop();
        drive.stopMotors();
    }

    @Override
    public boolean isFinished() {
        return m_timer.hasElapsed(m_trajectory.getTotalTimeSeconds());
    }
}
