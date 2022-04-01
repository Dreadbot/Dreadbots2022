package frc.robot.command.autonomous;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.command.shooter.ShooterCommands;

// Construct with `.perpetually().withTimeout(15)` so that it
// continuously collects and shoots for the full auton time
class BallTrackingAuton extends SequentialCommandGroup {
    private Drive drive;
    private Shooter shooter;
    private Intake intake;

    public BallTrackingAuton(Drive drive, Shooter shooter, Intake intake) {
        this.drive = drive;
        this.shooter = shooter;
        this.intake = intake;
        
        addRequirements(drive, shooter, intake);
        addCommands(
            new CollectBallCommand(drive),
            new ShooterCommands.HighShoot(shooter, intake)
        );
    }
}

class CollectBallCommand extends CommandBase {
    private Drive drive;
    private double distance;
    private double angle;
    private double direction;

    public CollectBallCommand(Drive drive) {
        this.drive = drive;
        addRequirements(drive);
    }

    @Override
    public void execute() {
        distance = SmartDashboard.getNumber("RelativeDistanceToBall", 0);
        angle = SmartDashboard.getNumber("RelativeAngleToBall", 0);
        direction = angle/angle;
        drive.drivePolar(.2, angle, .2 * direction); // Ensures that we turn the fastest way
    }

    @Override
    public boolean isFinished() {
        return distance == 0;
    }

    @Override
    public void end(boolean isIntertupted) {
        drive.resetEncoders();
        drive.drivePolar(0, 0, 0);
    }
}
