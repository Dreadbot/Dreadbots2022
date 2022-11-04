package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DreadbotMecanumDrive;

public class RotateClimbingArmVerticalCommand extends CommandBase {
    private Climber climber;
    private DreadbotMecanumDrive drive;
    private boolean rotated;
    public RotateClimbingArmVerticalCommand(Climber climber, DreadbotMecanumDrive drive) {
        this.climber = climber;
        this.drive = drive;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        rotated = false;

    }
    @Override
    public void execute() {
        if(drive.getGyroscope().getPitch() >= -Constants.NEUTRAL_CLIMBER_ROLL) {
            climber.rotateClimbingHookVertical();
            rotated = true;
        }
    }
    @Override
    public boolean isFinished() {
        return rotated;
    }
}
