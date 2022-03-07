package frc.robot.command.climber;
import frc.robot.subsystem.Climber;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CalibrateArmCommand extends ParallelRaceGroup{
    private Climber climber;
    public CalibrateArmCommand(Climber climber) {
        this.climber = climber;
        addCommands(    
            new RetractArmCommand(climber),
            new WaitCommand(15)
        );
        addRequirements(climber);
    }
    @Override
    public void initialize() {
        if(!climber.getBottomLimitSwitch()) {
            climber.retractArm();
        }
    }
    @Override
    public boolean isFinished() {
        return climber.getBottomLimitSwitch();
    }
    @Override
    public void end(boolean interupted) {
        if(!interupted) climber.zeroEncoderPosition();
        climber.stopMotors();
    }
}
