package frc.robot.command.climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Climber;

public class ExtendArmCommand extends CommandBase {
    private Climber climber;
    public ExtendArmCommand(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        if(!climber.getTopLimitSwitch())
            climber.extendArm();
    }
    @Override
    public boolean isFinished() {
        return climber.getTopLimitSwitch();
    }
    @Override
    public void end(boolean interupted) {
        climber.stopMotor();
    }
}
