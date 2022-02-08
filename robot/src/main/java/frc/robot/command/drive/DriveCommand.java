package frc.robot.command.drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;

public class DriveCommand extends CommandBase {
    private final Drive drive;

    private final DoubleSupplier joystickForwardAxis;
    private final DoubleSupplier joystickLateralAxis;
    private final DoubleSupplier joystickRotationalAxis;

    public DriveCommand(Drive drive, DoubleSupplier joystickForwardAxis, DoubleSupplier joystickLateralAxis, DoubleSupplier joystickRotationalAxis) {
        this.drive = drive;

        this.joystickForwardAxis = joystickForwardAxis;
        this.joystickLateralAxis = joystickLateralAxis;
        this.joystickRotationalAxis = joystickRotationalAxis;

        addRequirements(drive);
    }

    @Override
    public void initialize() {
        drive.driveCartesian(joystickForwardAxis.getAsDouble(), 
            joystickLateralAxis.getAsDouble(),
            joystickRotationalAxis.getAsDouble());
    }

    @Override
    public void execute() {
        drive.driveCartesian(joystickForwardAxis.getAsDouble(), 
            joystickLateralAxis.getAsDouble(),
            joystickRotationalAxis.getAsDouble());
    }
}
