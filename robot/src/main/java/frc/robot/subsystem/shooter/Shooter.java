package frc.robot.subsystem.shooter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
    private final Feeder feeder;
    private final Flywheel flywheel;
    private final Hood hood;
    private final Turret turret;
    
    public Shooter(Feeder feeder, Flywheel flywheel, Hood hood, Turret turret) {
        this.feeder = feeder;
        this.flywheel = flywheel;
        this.hood = hood;
        this.turret = turret;
        
        SmartDashboard.putNumber("RPM", 0.0d);
    }

    public void feedBall() {
        if(!Constants.SHOOTER_ENABLED) return;

        feeder.feed();
    }

    public void rampFlywheelToSpeed(double rpm) {
        if(!Constants.SHOOTER_ENABLED) return;

        // TODO remove SmartDashboard settings, use vision/distance input
        double setPoint = SmartDashboard.getNumber("RPM", 0);
        flywheel.ramp(rpm);

        SmartDashboard.putNumber("Flywheel Velocity (RPM)", flywheel.getVelocity());
    }

    public void turnHoodToAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        hood.turnToAngle(angle);
    }

    public void turnTurretToAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        turret.turnToAngle(angle);
    }

    public void idle() {
        if(!Constants.SHOOTER_ENABLED) return;

        // These processes should only run when a shoot command is registered.
        flywheel.idle();
        feeder.idle();

        SmartDashboard.putNumber("Flywheel Velocity (RPM)", flywheel.getVelocity());
    }

    public Feeder getFeeder() {
        return feeder;
    }
    
    public Flywheel getFlywheel() {
        return flywheel;
    }

    public Hood getHood() {
        return hood;
    }

    public Turret getTurret() {
        return turret;
    }
}
