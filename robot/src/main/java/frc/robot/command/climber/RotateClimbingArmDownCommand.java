package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class RotateClimbingArmDownCommand extends CommandBase {
    private Climber climber;
    public RotateClimbingArmDownCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        climber.rotateClimbingHookDown();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}

