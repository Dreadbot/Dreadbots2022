package frc.robot.command.autonomous;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.command.drive.DriveCommand;
import frc.robot.subsystem.Drive;


public class BasicAuton extends SequentialCommandGroup {
    private Drive drive;

    public BasicAuton(Drive drive){
        this.drive = drive;
        addCommands(new AutonDrive(drive));
        addRequirements(drive);
    }

}

class AutonDrive extends CommandBase {
    private Drive drive;
    private double orginalEncoderValue;
    private double driveDistance = 200.0; 

    public AutonDrive(Drive drive){
        this.drive = drive;
        addRequirements(drive);
    }
    @Override
    public void initialize(){
        drive.resetEncoders();
        orginalEncoderValue = 0; //drive.getFrontEncoderAvg();
        SmartDashboard.putNumber("Orginal Encoder Value", orginalEncoderValue);
    }

    @Override
    public void execute(){
        drive.driveCartesian(.2, 0, 0);
    }

    @Override
    public boolean isFinished(){
        return Math.abs(drive.getFrontEncoderAvg())>= 20;
    }

    @Override
    public void end(boolean isIntertupted){
        drive.resetEncoders();
        drive.driveCartesian(0, 0, 0);
    }
}
