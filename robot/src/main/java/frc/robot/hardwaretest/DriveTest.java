package frc.robot.hardwaretest;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;

public class DriveTest extends CommandBase{
    private final Drive drive; 
    private double angle;
    private Timer timer; 
    public DriveTest(Drive drive) {
        this.drive = drive;
        this.timer = new Timer();

        addRequirements(drive);
    }
    @Override 
    public void initialize (){
        angle = 1.0d;

        timer.reset();
        timer.start();

        System.out.println("test!!!!");
    }
    @Override 
    public void execute (){
        angle = 72.0d * timer.get();
        System.out.println("Execute");
        if(angle > 180.0d) angle -= 360.0d;
        System.out.println("Angle: " + angle);
        drive.drivePolar(1.0d, angle, 0.0d);
    }
    @Override 
    public boolean isFinished (){
        System.out.println("is finished: " + (72*timer.get()>360));
        return 72*timer.get()>360;
    }
}
