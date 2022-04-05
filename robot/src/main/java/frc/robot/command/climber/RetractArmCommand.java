package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystem.Climber;

public class RetractArmCommand extends CommandBase{
    private Climber climber;

    private boolean isTraversal;

    public RetractArmCommand(Climber climber) {
        this.climber = climber;
        this.isTraversal = false;

        addRequirements(climber);
    }

    public RetractArmCommand(Climber climber, boolean isTraversal) {
        this.climber = climber;
        this.isTraversal = isTraversal;

        addRequirements(climber);
    }

    @Override
    public void initialize() {
        if(!climber.getBottomLimitSwitch())
            climber.retractArm();
    }
    @Override
    public boolean isFinished() {
        if(isTraversal)
            return climber.getWinchPosition() <= 0.5 * Constants.CLIMBER_RANGE;

        return climber.getBottomLimitSwitch();
    }

    @Override
    public void end(boolean interupted) {
        climber.stopMotors();

//        climber.updateRetractedPosition();
    }
}
