package frc.robot.command.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    private final Intake intake;

    public IntakeCommand(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        System.out.println("intaking");
        if(intake.isOuttaking()) return;
        
        intake.intake();
    }

    @Override
    public void end(boolean interrupted) {
        intake.idle();
    }
}
