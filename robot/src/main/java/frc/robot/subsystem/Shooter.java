package frc.robot.subsystem;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Shooter extends Subsystem {
    private final CANSparkMax flywheelMotor;
    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;

    private SparkMaxPIDController pidController;
    private RelativeEncoder encoder;
    public ShootingState state;
    public double lastSetPoint;
    private double distanceToGoal;

    private SlewRateLimiter filter = new SlewRateLimiter(500); // 2000 RPM/s
    
    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        super("Shooter");
        
        this.flywheelMotor = flywheelMotor;
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;

        if(!Constants.SHOOTER_ENABLED) {
            flywheelMotor.close();
            hoodMotor.close();
            turretMotor.close();
            
            return;
        }

        flywheelMotor.restoreFactoryDefaults();
        flywheelMotor.setIdleMode(IdleMode.kCoast);
        pidController = flywheelMotor.getPIDController();
        encoder = flywheelMotor.getEncoder();

        pidController.setP(Constants.FLYWHEEL_P_GAIN);
        pidController.setI(Constants.FLYWHEEL_I_GAIN);
        pidController.setD(Constants.FLYWHEEL_D_GAIN);
        pidController.setIZone(Constants.FLYWHEEL_I_ZONE);
        pidController.setFF(Constants.FLYWHEEL_FF_GAIN);
        pidController.setOutputRange(Constants.FLYWHEEL_MIN_OUTPUT, Constants.FLYWHEEL_MAX_OUTPUT); 

        SmartDashboard.putNumber("RPM", 0.0d);
    }

    public void setTurretAngle(double speed) {
        if(!Constants.SHOOTER_ENABLED) return;

        turretMotor.set(speed);
    }

    public void shoot() {
        if(!Constants.SHOOTER_ENABLED) return;

        double setPoint = SmartDashboard.getNumber("RPM", 0);
        if(setPoint != lastSetPoint) {
            // Prevents the motor from going beyond 5700RPM
            setPoint = Math.min(setPoint, Constants.FLYWHEEL_MAX_RPM);

            // Commands the motor to approach the requested angular speed.
            pidController.setReference(setPoint, ControlType.kVelocity);
            
            // This sets the value of the initial RPM for when the flywheel ramps down.
            filter.reset(setPoint);
            
            SmartDashboard.putNumber("RPM", setPoint);
            lastSetPoint = setPoint;
        }

        SmartDashboard.putNumber("Flywheel Velocity (RPM)", encoder.getVelocity());
    }

    public void idle() {
        // Commands the motor to ramp down linearly (slew) to the requested 0RPM.
        pidController.setReference(filter.calculate(0), ControlType.kVelocity);

        lastSetPoint = 0;

        SmartDashboard.putNumber("Flywheel Velocity (RPM)", encoder.getVelocity());
    }

    @SuppressWarnings("unused")
    private void setHoodPosition(double hoodPosition) {
        if(!Constants.SHOOTER_ENABLED) return;

        //TODO
    }

    @SuppressWarnings("unused")
    private void setFlywheelRPM(double revolutionsPerMinute) {
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

        flywheelMotor.close();
        hoodMotor.close();
        turretMotor.close();
    }
    @Override
    protected void stopMotors() {
        if(!Constants.SHOOTER_ENABLED) return;
        
        flywheelMotor.stopMotor();
        hoodMotor.stopMotor();
        turretMotor.stopMotor();
    }
    public CANSparkMax getFlywheelMotor() {
        return flywheelMotor;
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
    @SuppressWarnings("unused")
    private void speedControllerFlywheel(double rpm) {
        //TODO
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
