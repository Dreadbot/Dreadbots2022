package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.ClimbLevel;

public class TraverseClimb  extends SequentialCommandGroup {
    public TraverseClimb(Climber climber, Turret turret) {
        addCommands(
            new SlightArmExtend(climber),
            new RotateClimbingArmDownCommand(climber),
            new ExtendArmCommand(climber, ClimbLevel.HIGH),
            new RotateClimbingArmVerticalCommand(climber),
            new WaitCommand(1),
            new SlightArmRetract(climber),
            new RotateNeutralHookDownCommand(climber),
            new RetractArmCommand(climber)
        );
    }
}
