package frc.robot.subsystem.shooter;

import frc.robot.subsystem.DreadbotSubsystem;

public class Shooter extends DreadbotSubsystem {
    private Feeder feeder;
    private Flywheel flywheel;
    private Hood hood;
    private Turret turret;

    /**
     * Disabled constructor
     */
    public Shooter() {
        disable();
    }

    public Shooter(Feeder feeder, Flywheel flywheel, Hood hood, Turret turret) {
        this.feeder = feeder;
        this.flywheel = flywheel;
        this.hood = hood;
        this.turret = turret;
    }

    public void feedBall() {
        System.out.println("SHOOTER CLASS CALL");
        if(isDisabled()) return;

        System.out.println("SHOOTER CLASS CALL ENABLED");

        feeder.feed();
    }

    public void setFlywheelVelocity(double velocity) {
        if(isDisabled()) return;

        flywheel.setVelocity(velocity);
    }

    public void setHoodAngle(double angle) {
        if(isDisabled()) return;

        hood.setAngle(angle);
    }

    public void setTurretAngle(double angle) {
        if(isDisabled()) return;

        turret.setAngle(angle);
    }

    public double getFlywheelVelocity() {
        if(isDisabled()) return 0.0d;

        return flywheel.getVelocity();
    }

    public double getHoodAngle() {
        if(isDisabled()) return 0.0d;

        return hood.getAngle();
    }

    public double getTurretAngle() {
        if(isDisabled()) return 0.0d;

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
        if(isDisabled()) return;

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
