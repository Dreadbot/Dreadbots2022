package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.VisionInterface;

public class ShootCommand extends SequentialCommandGroup {
    public ShootCommand(Shooter shooter) {
        SmartDashboard.putNumber("RPM", 0.0d);
        
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
        double velocity = VisionInterface.getFlywheelVelocity();

        shooter.setFlywheelVelocity(velocity);
    }

    @Override
    public boolean isFinished() {
        double setPoint = VisionInterface.getFlywheelVelocity();
        double actual = shooter.getFlywheelVelocity();

        return Math.abs(setPoint - actual) <= 10.0;
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
        double turretPosition = shooter.getTurretAngle();
        double relative = VisionInterface.getRequestedTurretAngle();

        turretActualTarget = relative;

        shooter.setTurretAngle(relative);
    }

    @Override
    public boolean isFinished() {
        double turretPosition = shooter.getTurretAngle();

        System.out.println("turret: " + Boolean.toString(Math.abs(turretActualTarget - turretPosition) <= 10.0d));
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
        double relative = VisionInterface.getRequestedHoodAngle();

        hoodActualTarget = relative;

        shooter.setHoodAngle(relative);
    }

    @Override
    public boolean isFinished() {
        double turretPosition = shooter.getHoodAngle();

        double error = hoodActualTarget - turretPosition;
        System.out.println("error: " + error);

        //System.out.println("hood: " + Boolean.toString(Math.abs(hoodActualTarget - turretPosition) <= 10.0d));
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