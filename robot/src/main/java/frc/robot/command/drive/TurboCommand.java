package frc.robot.command.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurboCommand extends CommandBase{
    private DriveCommand driveCommand;
    public TurboCommand(DriveCommand driveCommand) {
        this.driveCommand = driveCommand;
    }
    @Override
    public void initialize() {}

    @Override
    //activate Sanic
    public void execute() {
        driveCommand.enableTurbo();
    }

    @Override
    //no more Sanic :(
    public void end(boolean interrupted) {
        driveCommand.disableTurbo();
    }
    
}
