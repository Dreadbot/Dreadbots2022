package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.DreadbotMecanumDrive;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.ClimbLevel;

public class TraverseClimb  extends SequentialCommandGroup {
    public TraverseClimb(Climber climber, DreadbotMecanumDrive drive, Turret turret) {
        addCommands(
            new SlightArmExtend(climber, drive),
            new RotateClimbingArmDownCommand(climber),
            new ExtendArmCommand(climber, drive, ClimbLevel.HIGH),
            new RotateClimbingArmVerticalCommand(climber, drive),
            new SlightArmRetract(climber),
            new RotateNeutralHookDownCommand(climber),
            new RetractArmCommand(climber, true)
        );
    }
}
