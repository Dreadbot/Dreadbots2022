package frc.robot.command.autonomous;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;

public class VoltageFeedControlTest extends CommandBase {
    private final Drive drive;

    public VoltageFeedControlTest(Drive drive) {
        this.drive = drive;
        addRequirements(drive);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        drive.setWheelVoltages(3.0, 3.0, 3.0, 3.0);
        drive.printMotorVelocities();
    }

    @Override
    public void end(boolean interrupted) {
        //drive.stopMotors();
    }

    int i =0;
    @Override
    public boolean isFinished() {
        if(i < 100)
        {
            i++;
            return false;
        }
        return true;
    }
}
