package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.subsystem.shooter.Shooter;

public class ShootCommand extends SequentialCommandGroup {
    public ShootCommand(Shooter shooter) {
        addCommands(
            // Prepare the shooter system for the shot
            new ParallelCommandGroup(
                new FlywheelVelocityCommand(shooter),
                new TurretAngleCommand(shooter),
                new HoodAngleCommand(shooter)
            ),

            // Feed the ball, and shoot continuously.
            new FeedBallCommand(shooter)
        );
    }
}

class FlywheelVelocityCommand extends CommandBase {
    private final Shooter shooter;

    public FlywheelVelocityCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getFlywheel());
    }

    @Override
    public void execute() {
        // TODO remove SmartDashboard settings, use vision/distance input
        double setPoint = SmartDashboard.getNumber("RPM", 0);

        shooter.setFlywheelVelocity(setPoint);
        
        SmartDashboard.putNumber("Flywheel Velocity (RPM)", shooter.getFlywheelVelocity());
    }

    @Override
    public boolean isFinished() {
        double setPoint = SmartDashboard.getNumber("RPM", 0);
        double actual = shooter.getFlywheelVelocity();

        return Math.abs(setPoint - actual) <= 15.0;
    }
}

class TurretAngleCommand extends CommandBase {
    private final Shooter shooter;

    private double turretActualTarget;

    private double lastVisionRelativeTarget;

    public TurretAngleCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getTurret());
    }

    @Override
    public void execute() {
        // TODO remove SmartDashboard settings, use vision/distance input
        double turretPosition = shooter.getTurretAngle();
        double relative = SmartDashboard.getNumber("Turret Angle", 0);
        if(lastVisionRelativeTarget != relative) {
            turretActualTarget = turretPosition + relative;

            lastVisionRelativeTarget = relative;
        }

        shooter.setTurretAngle(turretActualTarget);
    }

    @Override
    public boolean isFinished() {
        double turretPosition = shooter.getTurretAngle();

        return Math.abs(turretActualTarget - turretPosition) <= 10.0d;
    }
}

class HoodAngleCommand extends CommandBase {
    private final Shooter shooter;

    private double hoodActualTarget;

    private double lastVisionRelativeTarget;

    public HoodAngleCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getHood());
    }

    @Override
    public void execute() {
        double hoodPosition = shooter.getHoodAngle();
        double relative = SmartDashboard.getNumber("Turret Angle", 0);
        if(lastVisionRelativeTarget != relative) {
            hoodActualTarget = hoodPosition + relative;

            lastVisionRelativeTarget = relative;
        }

        shooter.setTurretAngle(hoodActualTarget);
    }

    @Override
    public boolean isFinished() {
        double turretPosition = shooter.getTurretAngle();

        return Math.abs(hoodActualTarget - turretPosition) <= 10.0d;
    }
}

class FeedBallCommand extends CommandBase { 
    private final Shooter shooter;

    public FeedBallCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getFeeder());
    }

    @Override
    public void execute() {
        shooter.feedBall();
    }
}