package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class RotateClimbingArmVerticalCommand extends CommandBase {
    private Climber climber;
    public RotateClimbingArmVerticalCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        climber.rotateClimbingHookVertical();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
