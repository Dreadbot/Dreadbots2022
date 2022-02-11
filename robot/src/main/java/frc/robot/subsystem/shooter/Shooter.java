package frc.robot.subsystem.shooter;
import frc.robot.Constants;

public class Shooter {
    private final Feeder feeder;
    private final Flywheel flywheel;
    private final Hood hood;
    private final Turret turret;
    
    public Shooter(Feeder feeder, Flywheel flywheel, Hood hood, Turret turret) {
        this.feeder = feeder;
        this.flywheel = flywheel;
        this.hood = hood;
        this.turret = turret;
    }

    public void feedBall() {
        if(!Constants.SHOOTER_ENABLED) return;

        feeder.feed();
    }

    public void setFlywheelSpeed(double velocity) {
        if(!Constants.SHOOTER_ENABLED) return;

        flywheel.setVelocity(velocity);
    }

    public void setHoodAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        hood.turnToAngle(angle);
    }

    public void setTurretAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        turret.setAngle(angle);
    }

    public double getFlywheelVelocity() {
        if(!Constants.SHOOTER_ENABLED) return 0.0d;

        return flywheel.getVelocity();
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
