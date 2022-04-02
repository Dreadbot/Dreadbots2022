package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.command.shooter.TurretCommands;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.shooter.Turret;

public class TraverseClimb  extends SequentialCommandGroup {
    public TraverseClimb(Climber climber, Turret turret) {
        addCommands(
            new ScheduleCommand(new TurretCommands.TurnToClimb(turret)),
            new ExtendArmCommand(climber),
            new RotateClimbingArmVerticalCommand(climber),
            new WaitCommand(.4),
            new RetractArmCommand(climber),
            new WaitCommand(.2),
            new RotateNeutralHookVerticalCommand(climber)
        );
    }
}
