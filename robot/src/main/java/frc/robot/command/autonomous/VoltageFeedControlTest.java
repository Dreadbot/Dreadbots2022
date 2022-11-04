package frc.robot.command.autonomous;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DreadbotMecanumDrive;

public class VoltageFeedControlTest extends CommandBase {
    private final DreadbotMecanumDrive drive;

    public VoltageFeedControlTest(DreadbotMecanumDrive drive) {
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
