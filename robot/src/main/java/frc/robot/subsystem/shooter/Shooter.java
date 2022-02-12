package frc.robot.subsystem.shooter;
import frc.robot.Constants;
import frc.robot.util.MotorSafeSystem;

public class Shooter implements AutoCloseable, MotorSafeSystem {
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

    public void setFlywheelVelocity(double velocity) {
        if(!Constants.SHOOTER_ENABLED) return;

        flywheel.setVelocity(velocity);
    }

    public void setHoodAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        hood.setAngle(angle);
    }

    public void setTurretAngle(double angle) {
        if(!Constants.SHOOTER_ENABLED) return;

        turret.setAngle(angle);
    }

    public double getFlywheelVelocity() {
        if(!Constants.SHOOTER_ENABLED) return 0.0d;

        return flywheel.getVelocity();
    }

    public double getHoodAngle() {
        if(!Constants.SHOOTER_ENABLED) return 0.0d;

        return hood.getAngle();
    }

    public double getTurretAngle() {
        if(!Constants.SHOOTER_ENABLED) return 0.0d;

        return turret.getAngle();
    }

    @Override
    public void close() throws Exception {
        feeder.close();
        flywheel.close();
        hood.close();
        turret.close();
    }

    @Override
    public void stopMotors() {
        if(!Constants.SHOOTER_ENABLED) return;

        feeder.stopMotors();
        flywheel.stopMotors();
        hood.stopMotors();
        turret.stopMotors();
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
