package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystem.Climber;

public class AutonomousClimberCommand extends SequentialCommandGroup{
    public AutonomousClimberCommand(Climber climber){
        //Asssumes we are below the bar with the power hook down and neutral hooks back
//        addCommands(
//            new ExtendArmCommand(climber),
//            new RotateClimbingArmVerticalCommand(climber),
//            new RetractArmCommand(climber),
//            new RotateNeutralHookVerticalCommand(climber)
//        );
        //Old climber code 
        //Assumes we are below bar but with power hooks on the medium bar
         for(int i = 0; i < 1; i++){
             addCommands(
               new SlightArmExtend(climber), // helps with getting arm off of bar
               new RotateClimbingArmDownCommand(climber),
               new ExtendArmCommand(climber),// retract arm back to grab hook
               new RotateClimbingArmVerticalCommand(climber),
               new SlightArmRetract(climber),
               //new WaitCommand(.2),
               new WaitCommand(.2),
               new RotateNeutralHookDownCommand(climber),
               new RetractArmCommand(climber),
               new WaitCommand(.2),
               new RotateNeutralHookVerticalCommand(climber),
               new WaitCommand(.5)
             );
         }
        //  addCommands(
        //      new RetractArmCommand(climber),
        //      new WaitCommand(.2),
        //      new RotateNeutralHookVerticalCommand(climber)
        //  );
    }
}

class SlightArmExtend extends ParallelRaceGroup{
    public SlightArmExtend(Climber climber){
        addCommands(
            new ExtendArmCommand(climber),
            new WaitCommand(.5)
        );
    }
}

class SlightArmRetract extends ParallelRaceGroup{
    public SlightArmRetract(Climber climber){
        addCommands(
            new RetractArmCommand(climber),
            new WaitCommand(.5)
        );
    }
}
