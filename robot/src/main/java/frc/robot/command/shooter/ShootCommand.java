package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.ColorSensor;
import frc.robot.subsystem.shooter.Feeder;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.VisionInterface;

public class ShootCommand extends SequentialCommandGroup {
    //private Color ballColor;
    //private Color teamColor;
    private final ColorSensor dreadbotColorSensor;
    public static boolean correctColor; // Change to not static later?

    public ShootCommand(Shooter shooter, ColorSensor dreadbotColorSensor, Color teamColor) {
        SmartDashboard.putNumber("RPM", 0.0d);
        this.dreadbotColorSensor = dreadbotColorSensor;
        //this.teamColor = teamColor;
        GetBallColorCommand getBallColorCommand = new GetBallColorCommand(dreadbotColorSensor, teamColor);
        addCommands(
            getBallColorCommand,
            // Prepare the shooter system for the shot
            new ParallelCommandGroup(
                new FlywheelVelocityCommand(shooter),
                new TurretAngleCommand(shooter),
                new HoodAngleCommand(shooter)
            ),

            // Feed the ball, and shoot continuously.
            new FeedBallCommand(shooter, dreadbotColorSensor, getBallColorCommand.getBallColor())
        );
    }
}

class GetBallColorCommand extends CommandBase {
    private Color ballColor;
    private final ColorSensor dreadbotColorSensor;
    private final Color teamColor;

    public GetBallColorCommand(ColorSensor dreadbotColorSensor, Color teamColor){
        this.dreadbotColorSensor = dreadbotColorSensor;
        this.teamColor = teamColor;
    }

    public Color getBallColor(){
        return ballColor;
    }

    @Override
    public boolean isFinished() {
        ballColor = dreadbotColorSensor.getBallColor();
        if (ballColor == teamColor)
            ShootCommand.correctColor = true;
        else
            ShootCommand.correctColor = false;
        return ballColor != null;
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
        double velocity = VisionInterface.getFlywheelVelocity(ShootCommand.correctColor);

        shooter.setFlywheelVelocity(velocity);
    }

    @Override
    public boolean isFinished() {
        double setPoint = VisionInterface.getFlywheelVelocity(ShootCommand.correctColor);
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
        //double turretPosition = shooter.getTurretAngle();
        double relative = VisionInterface.getRequestedTurretAngle(ShootCommand.correctColor);

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
        //double hoodPosition = shooter.getHoodAngle();
        double relative = VisionInterface.getRequestedHoodAngle(ShootCommand.correctColor);

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
    private final ColorSensor dreadbotColorSensor;
    private Color orginalBallColor;

    public FeedBallCommand(Shooter shooter, ColorSensor dreadbotColorSensor, Color orginalBallColor) {
        this.shooter = shooter;
        this.dreadbotColorSensor = dreadbotColorSensor;
        this.orginalBallColor = orginalBallColor;

        addRequirements(shooter.getFeeder());
    }

    @Override
    public void execute() {
        shooter.feedBall();
    }

    @Override
    public boolean isFinished() {
        Color currentBallColor = dreadbotColorSensor.getBallColor();
        // Return true if different color ball, or if no ball is detected
        return currentBallColor == null || currentBallColor != orginalBallColor;
    }
}