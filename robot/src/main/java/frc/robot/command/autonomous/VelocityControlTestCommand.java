package frc.robot.command.autonomous;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.DreadbotMecanumDrive;

public class VelocityControlTestCommand extends CommandBase {
    private final DreadbotMecanumDrive drive;

    public VelocityControlTestCommand(DreadbotMecanumDrive drive) {
        this.drive = drive;

        addRequirements(drive);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        drive.setChassisSpeeds(new ChassisSpeeds(1.5, 0, 0));
    }

    @Override
    public void end(boolean interrupted) {
        drive.setChassisSpeeds(new ChassisSpeeds(0, 0, 0));
        drive.stopMotors();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
