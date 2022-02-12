package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystem.Climber;

public class AutonomousClimberCommand extends SequentialCommandGroup{
    public AutonomousClimberCommand(Climber climber){
        //Assumes we are below bar but with power hooks on the bar
        for(int i = 0; i < 2; i++){
            addCommands(
              new RetractArmCommand(climber),
              new WaitCommand(.2),
              new RotateNeutralHookVerticalCommand(climber),
              new WaitCommand(.5),
              new SlightArmExtend(climber),// helps with getting arm off of bar
              new RotateClimbingHookDownCommand(climber),
              new ExtendArmCommand(climber),// retract arm back to grab hook
              new SlightArmRetract(climber),
              new WaitCommand(.5),
              new RotateClimbingHookVerticalCommand(climber), 
              new WaitCommand(.5),
              new RotateNeutralHookDownCommand(climber)
            ); 
        }
        addCommands(
            new RetractArmCommand(climber),
            new WaitCommand(.2),
            new RotateNeutralHookVerticalCommand(climber)
            );
    }
}

class SlightArmExtend extends ParallelCommandGroup{
    public SlightArmExtend(Climber climber){
        addCommands(
            new ExtendArmCommand(climber),
            new WaitCommand(.5)
        );
    }
}

class SlightArmRetract extends ParallelCommandGroup{
    public SlightArmRetract(Climber climber){
        addCommands(
            new RetractArmCommand(climber),
            new WaitCommand(.5)
        );
    }
}
