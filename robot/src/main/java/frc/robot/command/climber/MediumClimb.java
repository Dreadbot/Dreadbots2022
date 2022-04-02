package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.shooter.Turret;

public class MediumClimb extends SequentialCommandGroup {
    public MediumClimb(Climber climber,Turret turret){
        
        //Assumes we are below the bar with power hook retracted and down and neutral hooks down

        addCommands(
            new ScheduleCommand(new TurretCommands.TurnToClimb(turret)),
            new ExtendArmCommand(climber),
            new RotateNeutralHookVerticalCommand(climber),
            new WaitCommand(.4),
            new RetractArmCommand(climber),
            new WaitCommand(.2), 
            new RotateClimbingArmDownCommand(climber)
        );
    }
    
}
