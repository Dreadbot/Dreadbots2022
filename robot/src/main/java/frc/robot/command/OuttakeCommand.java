package frc.robot.command;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class OuttakeCommand extends CommandBase{
    private final Intake intake;

    public OuttakeCommand(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        if(intake.isIntaking()) return;
        
        intake.outtake();
    }

}
