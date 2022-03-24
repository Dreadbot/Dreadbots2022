package frc.robot.command.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Feeder;

public class OuttakeCommand extends CommandBase{
    private final Intake intake;
    private final Feeder feeder;

    public OuttakeCommand(Intake intake, Feeder feeder) {
        this.intake = intake;
        this.feeder = feeder;

        addRequirements(intake, feeder);
    }

    @Override
    public void initialize() {
        if(intake.isIntaking()) return;
        
        intake.outtake();
        feeder.outtake();
    }

    @Override
    public void end(boolean interrupted) {
        feeder.idle();
        intake.idle();
    }
}
