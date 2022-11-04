package frc.robot.subsystem.shooter;

import frc.robot.Constants;

public class Shooter {
    private final Feeder feeder;
    private final Flywheel flywheel;
    private final Hood hood;
    private final Turret turret;
    private final ColorSensor colorSensor;

    public Shooter(Feeder feeder, Flywheel flywheel, Hood hood, Turret turret, ColorSensor colorSensor) {
        if (!Constants.SHOOTER_ENABLED) {
            return;
        }
        this.feeder = feeder;
        this.flywheel = flywheel;
        this.hood = hood;
        this.turret = turret;
        this.colorSensor = colorSensor;
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
