package frc.robot.command.autonomous;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystem.Drive;

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
    private final Drive drive;

    /**
     * Constructs a new PPMecanumControllerCommand that when executed will follow the provided
     * trajectory. The user should implement a velocity PID on the desired output wheel velocities.
     *
     * <p>Note: The controllers will *not* set the outputVolts to zero upon completion of the path -
     * this is left to the user, since it is not appropriate for paths with non-stationary end-states.
     *
     * @param trajectory The Pathplanner trajectory to follow.
     * @param pose A function that supplies the robot pose - use one of the odometry classes to
     *     provide this.
     * @param kinematics The kinematics for the robot drivetrain.
     * @param xController The Trajectory Tracker PID controller for the robot's x position.
     * @param yController The Trajectory Tracker PID controller for the robot's y position.
     * @param thetaController The Trajectory Tracker PID controller for angle for the robot.
     * @param maxWheelVelocityMetersPerSecond The maximum velocity of a drivetrain wheel.
     * @param outputWheelSpeeds A MecanumDriveWheelSpeeds object containing the output wheel speeds.
     * @param requirements The subsystems to require.
     */
    public TrajectoryAuton(
        PathPlannerTrajectory trajectory,
        Supplier<Pose2d> pose,
        MecanumDriveKinematics kinematics,
        PIDController xController,
        PIDController yController,
        ProfiledPIDController thetaController,
        double maxWheelVelocityMetersPerSecond,
        Consumer<MecanumDriveWheelSpeeds> outputWheelSpeeds,
        Drive drive) {
        m_trajectory = trajectory;
        m_pose = pose;
        m_kinematics = kinematics;
        this.drive = drive;

        m_controller = new HolonomicDriveController(xController, yController, thetaController);

        m_maxWheelVelocityMetersPerSecond = maxWheelVelocityMetersPerSecond;

        m_outputWheelSpeeds = outputWheelSpeeds;

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
        var desiredState = (PathPlannerTrajectory.PathPlannerState) m_trajectory.sample(curTime);

        var targetChassisSpeeds =
            m_controller.calculate(m_pose.get(), desiredState, desiredState.holonomicRotation);
        targetChassisSpeeds.omegaRadiansPerSecond *= .55;
        targetChassisSpeeds.vyMetersPerSecond *= .55;
        targetChassisSpeeds.vxMetersPerSecond *= .55;
        var targetWheelSpeeds = m_kinematics.toWheelSpeeds(targetChassisSpeeds);

        targetWheelSpeeds.desaturate(m_maxWheelVelocityMetersPerSecond);

        m_outputWheelSpeeds.accept(targetWheelSpeeds);


    }

    @Override
    public void end(boolean interrupted) {
        m_timer.stop();
    }

    @Override
    public boolean isFinished() {
        return m_timer.hasElapsed(m_trajectory.getTotalTimeSeconds());
    }
}
