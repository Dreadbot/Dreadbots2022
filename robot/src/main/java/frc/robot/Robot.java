// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.logging.PowerLogger;

import java.util.logging.Logger;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private RobotContainer robotContainer;
    public static final Logger LOGGER = Logger.getLogger(Robot.class.getName());
    private PowerLogger powerLogger;
    private Command autonomousCommand;

    @Override
    public void robotInit() {
        CameraServer.startAutomaticCapture(0);
        robotContainer = new RobotContainer();
        // powerLogger initialization moved to teleopInit so it's reset each time it's re-enabled
        // powerLogger = new PowerLogger();
    }

    @Override
    public void autonomousInit() {
        autonomousCommand = robotContainer.getAutonomousCommand();

        // schedule the autonomous command
        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {
        // Command based robot, periodic handled by command scheduler
    }

    @Override
    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        robotContainer.calibrate();

        powerLogger = new PowerLogger();
    }

    @Override
    public void teleopPeriodic() {
        powerLogger.logPower();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void robotPeriodic() {
        // Command based robot, run the scheduler so it can execute the active Commands
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        powerLogger.close();
    }

    @Override
    public void disabledPeriodic() {
    }
}
