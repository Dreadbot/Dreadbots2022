package frc.robot.command.drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.Drive;
import frc.robot.util.SensitivityController;

public class DriveCommand extends CommandBase {
    private final Drive drive;

    private final DoubleSupplier joystickForwardAxis;
    private final DoubleSupplier joystickLateralAxis;
    private final DoubleSupplier joystickRotationalAxis;

    private ChassisSpeeds commandedChassisSpeeds;

    private SensitivityController forwardSensitivityFilter;
    private SensitivityController lateralSensitivityFilter;
    private SensitivityController rotationalSensitivityFilter;

    private SlewRateLimiter slewRateLimiter;

    public DriveCommand(Drive drive, DoubleSupplier joystickForwardAxis, DoubleSupplier joystickLateralAxis, DoubleSupplier joystickRotationalAxis) {
        this.drive = drive;

        this.joystickForwardAxis = joystickForwardAxis;
        this.joystickLateralAxis = joystickLateralAxis;
        this.joystickRotationalAxis = joystickRotationalAxis;

        this.commandedChassisSpeeds = new ChassisSpeeds();

        setupFilters();

        addRequirements(drive);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        // Calculate the forward/backward axis gain
        double forwardAxis = -joystickForwardAxis.getAsDouble();
        forwardAxis = MathUtil.applyDeadband(forwardAxis, 0.03d);
//        forwardAxis = forwardSensitivityFilter.calculate(forwardAxis);
//        forwardAxis = slewRateLimiter.calculate(forwardAxis);

        // Calculate the side-to-side axis gain
        double lateralAxis = -joystickLateralAxis.getAsDouble();
        lateralAxis = MathUtil.applyDeadband(lateralAxis, 0.03d);
//        lateralAxis = lateralSensitivityFilter.calculate(lateralAxis);

        // Calculate the rotational gain
        double rotationalAxis = -joystickRotationalAxis.getAsDouble();
        rotationalAxis = MathUtil.applyDeadband(rotationalAxis, 0.03d);
//        rotationalAxis = rotationalSensitivityFilter.calculate(rotationalAxis);

        commandedChassisSpeeds.vxMetersPerSecond = forwardAxis * 3.5 * .55;
        commandedChassisSpeeds.vyMetersPerSecond = lateralAxis * 2.5 * .55;
        commandedChassisSpeeds.omegaRadiansPerSecond = rotationalAxis * .55 * Math.PI;

        // Input the drive code
//        drive.driveCartesian(forwardAxis, lateralAxis, rotationalAxis);
        drive.setChassisSpeeds(commandedChassisSpeeds);
    }

    private void setupFilters() {
        this.slewRateLimiter = new SlewRateLimiter(3);

        var forwardBuilder = new SensitivityController.Builder(-40.0, -40.0);
        this.forwardSensitivityFilter = forwardBuilder.build();

        var lateralBuilder = new SensitivityController.Builder(-40.0, -40.0)
            .minimumValues(0.1d, 0.1d)
            .maximumValues(0.5d, 0.5d);
        this.lateralSensitivityFilter = lateralBuilder.build();

        var rotationalBuilder = new SensitivityController.Builder(-40.0, -40.0);
        rotationalBuilder.maximumValues(0.3d, 0.3d);
        this.rotationalSensitivityFilter = rotationalBuilder.build();

        SmartDashboard.putData("forwardSensFilter", forwardSensitivityFilter);
        SmartDashboard.putData("lateralSensFilter", lateralSensitivityFilter);
        SmartDashboard.putData("rotateSensFilter", rotationalSensitivityFilter);
    }
}
