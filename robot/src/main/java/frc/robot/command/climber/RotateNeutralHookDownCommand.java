package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class RotateNeutralHookDownCommand extends CommandBase {
    private Climber climber;
    public RotateNeutralHookDownCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        climber.rotateNeutralHookDown();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
