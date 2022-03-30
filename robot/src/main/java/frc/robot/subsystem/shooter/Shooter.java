package frc.robot.subsystem.shooter;

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
