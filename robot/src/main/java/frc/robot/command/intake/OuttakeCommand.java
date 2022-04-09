package frc.robot.command.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Flywheel;

public class OuttakeCommand extends CommandBase{
    private final Intake   intake;
    private final Feeder   feeder;
    private final Flywheel flywheel;

    public OuttakeCommand(Intake intake, Feeder feeder, Flywheel flywheel) {
        this.intake = intake;
        this.feeder = feeder;
        this.flywheel = flywheel;

        addRequirements(intake, feeder, flywheel);
    }

    @Override
    public void initialize() {
        if(intake.isIntaking()) return;
        
        intake.outtake();
        feeder.outtake();
        flywheel.outtake();
    }

    @Override
    public void end(boolean interrupted) {
        feeder.idle();
        intake.idle();
        flywheel.idle();
    }
}
