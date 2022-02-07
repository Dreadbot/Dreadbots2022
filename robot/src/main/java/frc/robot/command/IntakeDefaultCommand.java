package frc.robot.command;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeDefaultCommand extends CommandBase {
    private final Intake intake;

    public IntakeDefaultCommand(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        intake.idle();
        System.out.println("idle");
    }
}
