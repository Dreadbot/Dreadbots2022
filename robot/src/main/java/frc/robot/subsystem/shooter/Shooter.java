package frc.robot.subsystem.shooter;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystem.Subsystem;

public class Shooter extends Subsystem {
    private final Flywheel flywheel;

    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;

    public ShootingState state;

    private double distanceToGoal;
    
    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        super("Shooter");
        
        this.flywheel = new Flywheel(flywheelMotor);
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;

        if(!Constants.SHOOTER_ENABLED) {
            flywheelMotor.close();
            hoodMotor.close();
            turretMotor.close();
            
            return;
        }
        
        SmartDashboard.putNumber("Flywheel Velocity (RPM)", 0.0d);
    }

    public void setTurretAngle(double speed) {
        if(!Constants.SHOOTER_ENABLED) return;

        turretMotor.set(speed);
    }

    public void shoot() {
        if(!Constants.SHOOTER_ENABLED) return;

        // TODO remove SmartDashboard settings, use vision/distance input
        double setPoint = SmartDashboard.getNumber("RPM", 0);
        flywheel.ramp(setPoint);

        SmartDashboard.putNumber("Flywheel Velocity (RPM)", flywheel.getVelocity());
    }

    public void idle() {
        if(!Constants.SHOOTER_ENABLED) return;

        flywheel.idle();
    }

    @SuppressWarnings("unused")
    private void setHoodPosition(double hoodPosition) {
        if(!Constants.SHOOTER_ENABLED) return;

        //TODO
    }

    public double getRequiredFlyWheelRPM() {
        return Constants.TO_RPM * Math.sqrt(Math.pow(calculateXVelocity(), 2) + Math.pow(distanceToGoal / calculateTimeToScore(), 2));
    }

    public double getRequiredHoodAngle() {
        return Math.atan(calculateXVelocity() / (distanceToGoal / calculateTimeToScore())) * 180 / Math.PI;
    }

    @Override
    public void close() throws Exception {
        if(!Constants.SHOOTER_ENABLED) return;

        flywheel.close();
        hoodMotor.close();
        turretMotor.close();
    }
    @Override
    protected void stopMotors() {
        if(!Constants.SHOOTER_ENABLED) return;
        
        flywheel.stopMotors();
        hoodMotor.stopMotor();
        turretMotor.stopMotor();
    }
    
    public Flywheel getFlywheel() {
        return flywheel;
    }

    public CANSparkMax getHoodMotor() {
        return hoodMotor;
    }

    public CANSparkMax getTurretMotor() {
        return turretMotor;
    }

    public void setDistanceToGoal(float dist) {
        distanceToGoal = dist;
    }

    private double calculateArcHeight() {
        return ((0.5d * distanceToGoal) + 2.5d);
    }

    public double calculateXVelocity() {
        return Math.sqrt(-2 * Constants.GRAVITY * (calculateArcHeight() - Constants.INITIAL_BALL_HEIGHT));
    }

    public double calculateTimeToScore() { 
        double xVel = calculateXVelocity();
        return (-xVel - Math.sqrt(Math.pow(xVel, 2) - 2 * Constants.GRAVITY * (Constants.INITIAL_BALL_HEIGHT - Constants.GOAL_HEIGHT))) / Constants.GRAVITY;
    }

    @SuppressWarnings("unused")
    private double hoodAngleToHoodPosition(double hoodAngle) {
        return 0d;
    }

    public double[] hoodCalibration() {
        //TODO
        return new double[2];
    }

    public enum ShootingState {
        Shooting,
        Aiming,
        SpinningUp,
        SpinningDown,
    }

    @SuppressWarnings("unused")
    private void setState(ShootingState newState) {
        state = newState;
    }
    
    @SuppressWarnings("unused")
    private ShootingState getState() {
        return state;
    }
}
