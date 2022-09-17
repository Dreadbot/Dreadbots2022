// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    public static final Logger LOGGER = Logger.getLogger(Robot.class.getName());


    FileWriter fileWriter;

    private RobotContainer robotContainer;

    private Command autonomousCommand;

    private PowerDistribution powerDistro = new PowerDistribution(22, ModuleType.kRev);

    @Override
    public void robotInit() {
        CameraServer.startAutomaticCapture(0);
        robotContainer = new RobotContainer();
        robotContainer.setTeamColor();
    }

    @Override
    public void autonomousInit() {
        robotContainer.setTeamColor();
        autonomousCommand = robotContainer.getAutonomousCommand();

        // schedule the autonomous command (example)
        if (autonomousCommand != null) {
          autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        robotContainer.setTeamColor();
        robotContainer.calibrate();

        if(Constants.VOLTAGE_REPORTING){
            try {
                fileWriter = new FileWriter("/tmp/PowerLog:" + new Date() + new Date().getTime() + ".txt");
                fileWriter.write("--PDP Power log--\n");
                fileWriter.write("Port Number:,");
                for(int i = 0; i < 24; i++){
                    fileWriter.write(i + ",");
                }
                    

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("BROKEN");
            }
        }
    }

    @Override
    public void teleopPeriodic() {
        if(powerDistro.getTotalCurrent() >= 40.0d && Constants. VOLTAGE_REPORTING){
            reportCurrents();   
        }
    }

    @Override
    public void testInit() {}

    @Override
    public void testPeriodic() {}

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disabledPeriodic() {}

    private void reportCurrents(){
        String powerOutput = "Power output:,";
        for(int i = 0; i < 24; i++){
            powerOutput += powerDistro.getCurrent(i) + ",";
        }  

        try {
            fileWriter.write(new Timestamp(new Date().getTime()) + " " + powerOutput + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
