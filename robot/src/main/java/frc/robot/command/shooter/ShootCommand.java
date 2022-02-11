package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.Shooter;

public class ShootCommand extends SequentialCommandGroup {
    public ShootCommand(Shooter shooter) {
        addCommands(
            // Prepare the shooter system for the shot
            new ParallelCommandGroup(
                new FlywheelVelocityCommand(shooter),
                new TurretAngleCommand(shooter),
                new HoodAngleCommand(shooter)
            ).withTimeout(1),

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

        shooter.setFlywheelSpeed(setPoint);
        
        SmartDashboard.putNumber("Flywheel Velocity (RPM)", shooter.getFlywheelVelocity());
    }
}

class TurretAngleCommand extends CommandBase {
    private final Shooter shooter;

    public TurretAngleCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getTurret());
    }

    @Override
    public void execute() {
        // TODO remove SmartDashboard settings, use vision/distance input
        double angle = SmartDashboard.getNumber("Turret Angle", 0);

        shooter.setTurretAngle(angle);
    }
}

class HoodAngleCommand extends CommandBase {
    private final Shooter shooter;

    public HoodAngleCommand(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter.getHood());
    }

    @Override
    public void execute() {
        // TODO remove SmartDashboard settings, use vision/distance input
        double angle = SmartDashboard.getNumber("Hood Angle", 0);

        shooter.setHoodAngle(angle);
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