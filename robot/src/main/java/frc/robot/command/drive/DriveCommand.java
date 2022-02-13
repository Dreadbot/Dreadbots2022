package frc.robot.command.drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;

public class DriveCommand extends CommandBase {
    private final Drive drive;

    private final DoubleSupplier joystickForwardAxis;
    private final DoubleSupplier joystickLateralAxis;
    private final DoubleSupplier joystickRotationalAxis;

    private final SlewRateLimiter filter = new SlewRateLimiter(0.5);

    public DriveCommand(Drive drive, DoubleSupplier joystickForwardAxis, DoubleSupplier joystickLateralAxis, DoubleSupplier joystickRotationalAxis) {
        this.drive = drive;

        this.joystickForwardAxis = joystickForwardAxis;
        this.joystickLateralAxis = joystickLateralAxis;
        this.joystickRotationalAxis = joystickRotationalAxis;

        addRequirements(drive);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        double forwardAxis = joystickForwardAxis.getAsDouble();
        double lateralAxis = joystickLateralAxis.getAsDouble();
        double rotationalAxis = -joystickRotationalAxis.getAsDouble();

        double fSign = Math.signum(forwardAxis);
        double lSign = Math.signum(lateralAxis);
        double rSign = Math.signum(rotationalAxis);

        forwardAxis *= forwardAxis * fSign * 0.75;
        lateralAxis *= lateralAxis * lSign;
        rotationalAxis *= rotationalAxis * rSign * 0.5;

        drive.driveCartesian(forwardAxis, lateralAxis, rotationalAxis);
    }
}
