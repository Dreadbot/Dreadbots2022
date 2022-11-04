package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DreadbotMecanumDrive;
import frc.robot.util.ClimbLevel;

public class ExtendArmCommand extends CommandBase {
    private Climber climber;
    private DreadbotMecanumDrive drive;

    private ClimbLevel climbLevel;

    public ExtendArmCommand(Climber climber, DreadbotMecanumDrive drive, ClimbLevel climbLevel) {
        this.climber = climber;
        this.drive = drive;
        this.climbLevel = climbLevel;

        addRequirements(climber);
    }

    @Override
    public void execute() {
        if(drive.getGyroscope().getPitch() < Constants.NEUTRAL_CLIMBER_ROLL) {
            climber.extendArm();
            return;
        }

        climber.idle();
    }

    @Override
    public boolean isFinished() {
        return climber.isPowerArmExtended(climbLevel);
    }

    @Override
    public void end(boolean interupted) {
        climber.stopMotors();
    }
}
