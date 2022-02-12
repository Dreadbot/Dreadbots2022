package frc.robot.command.climber;

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
              new RotateClimbingHookDownCommand(climber),
              new ExtendArmCommand(climber),
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
