package frc.robot.command.drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;
import frc.robot.util.SensitivityController;

public class DriveCommand extends CommandBase {
    private final Drive drive;

    private final DoubleSupplier joystickForwardAxis;
    private final DoubleSupplier joystickLateralAxis;
    private final DoubleSupplier joystickRotationalAxis;

    private final SensitivityController sensitivityFilter;

    @SuppressWarnings("unused")
    private final SlewRateLimiter filter = new SlewRateLimiter(3);

    public DriveCommand(Drive drive, DoubleSupplier joystickForwardAxis, DoubleSupplier joystickLateralAxis, DoubleSupplier joystickRotationalAxis) {
        this.drive = drive;

        this.joystickForwardAxis = joystickForwardAxis;
        this.joystickLateralAxis = joystickLateralAxis;
        this.joystickRotationalAxis = joystickRotationalAxis;

        this.sensitivityFilter = new SensitivityController();

        addRequirements(drive);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        double forwardAxis = joystickForwardAxis.getAsDouble();
        double lateralAxis = joystickLateralAxis.getAsDouble();
        double rotationalAxis = -joystickRotationalAxis.getAsDouble();

        forwardAxis = sensitivityFilter.calculate(forwardAxis);
        lateralAxis = sensitivityFilter.calculate(lateralAxis);
        rotationalAxis = sensitivityFilter.calculate(rotationalAxis);

        drive.driveCartesian(forwardAxis, lateralAxis, rotationalAxis);
    }
}
