package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class RetractArmCommand extends CommandBase{
    private Climber climber;
    public RetractArmCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        if(!climber.getBottomLimitSwitch())
            climber.retractArm();
    }
    @Override
    public boolean isFinished() {
        return climber.getBottomLimitSwitch();
    }
    @Override
    public void end(boolean interupted) {
        climber.stopMotor();
    }
}
