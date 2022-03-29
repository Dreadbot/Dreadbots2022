package frc.robot.subsystem.shooter;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.DreadbotSubsystem;

public class Shooter {
    private Feeder feeder;
    private Flywheel flywheel;
    private Hood hood;
    private Turret turret;
    private ColorSensor colorSensor;

    /**
     * Disabled constructor
     */
    public Shooter() {}

    public Shooter(Feeder feeder, Flywheel flywheel, Hood hood, Turret turret, ColorSensor colorSensor) {
        this.feeder = feeder;
        this.flywheel = flywheel;
        this.hood = hood;
        this.turret = turret;
        this.colorSensor = colorSensor;
    }

    public void feedBall() {
        feeder.feed();
    }

    public void setFlywheelVelocity(double velocity) {
        flywheel.setVelocity(velocity);
    }

    public void setHoodAngle(double angle) {
        hood.setAngle(angle);
    }

    public void setTurretAngle(double angle) {
        turret.setAngle(angle);
    }

    public double getFlywheelVelocity() {
        return flywheel.getTangentialVelocity();
    }

    public double getHoodAngle() {
        return hood.getAngle();
    }

    public double getTurretAngle() {
        return turret.getAngle();
    }


    public void close() throws Exception {
        feeder.close();
        flywheel.close();
        hood.close();
        turret.close();
    }

    public void stopMotors() {
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

    public ColorSensor getColorSensor() {
        return colorSensor;
    }
}
