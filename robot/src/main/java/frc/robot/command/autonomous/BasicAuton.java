package frc.robot.command.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.DreadbotMecanumDrive;


public class BasicAuton extends SequentialCommandGroup {
    private DreadbotMecanumDrive drive;

    public BasicAuton(DreadbotMecanumDrive drive){
        this.drive = drive;
        addCommands(new AutonDrive(drive));
        addRequirements(drive);
    }

}

class AutonDrive extends CommandBase {
    private DreadbotMecanumDrive drive;
    private double orginalEncoderValue;
    private double driveDistance = 200.0; 

    public AutonDrive(DreadbotMecanumDrive drive){
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
