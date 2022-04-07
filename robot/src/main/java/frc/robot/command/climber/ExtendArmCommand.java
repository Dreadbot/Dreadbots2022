package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.util.ClimbLevel;

public class ExtendArmCommand extends CommandBase {
    private Climber climber;
    private Drive drive;

    private ClimbLevel climbLevel;
    private double rollThreshold;

    public ExtendArmCommand(Climber climber, ClimbLevel climbLevel) {
        this.climber = climber;
        this.drive = drive;
        this.climbLevel = climbLevel;

        addRequirements(climber);
    }

    @Override
    public void initialize() {
//        if(drive.getGyroscope().getRoll > rollThreshold)
//            climber.idle();

        climber.extendArm();
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
