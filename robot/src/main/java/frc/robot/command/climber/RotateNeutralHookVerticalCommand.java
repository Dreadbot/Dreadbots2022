package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class RotateNeutralHookVerticalCommand extends CommandBase {
    private Climber climber;
    public RotateNeutralHookVerticalCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        System.out.println("Neutral Verticalling!");
        climber.rotateNeutralHookVertical();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
