package frc.robot.command.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    private final Intake intake;
    private final double power;

    public IntakeCommand(Intake intake) {
        this.intake = intake;
        this.power = 1.0d;

        addRequirements(intake);
    }

    public IntakeCommand(Intake intake, double power) {
        this.intake = intake;
        this.power = power;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        System.out.println("intaking");
        if(intake.isOuttaking()) return;
        
        intake.intake(power);
    }

    @Override
    public void end(boolean interrupted) {
        intake.idle();
    }
}
