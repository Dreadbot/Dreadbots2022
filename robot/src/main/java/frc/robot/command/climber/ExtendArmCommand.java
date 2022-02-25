package frc.robot.command.climber;

import java.lang.invoke.ConstantBootstraps;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
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
        if(Math.abs(climber.getEncoderPosition() - Constants.MAX_ENCODER_POSITION) <= 2) {
            return true;
        }
        return climber.getTopLimitSwitch();
    }
    @Override
    public void end(boolean interupted) {
        SmartDashboard.putNumber("Encoder Position", climber.getEncoderPosition());
        climber.stopMotor();
    }
}
