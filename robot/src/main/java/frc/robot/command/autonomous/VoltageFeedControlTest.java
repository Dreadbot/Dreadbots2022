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
        drive.setWheelVoltages(6.0, 6.0, 6.0, 6.0);
    }

    @Override
    public void end(boolean interrupted) {
        drive.stopMotors();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
