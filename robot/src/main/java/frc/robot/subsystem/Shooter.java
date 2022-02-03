package frc.robot.subsystem;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Shooter extends Subsystem {
    private final CANSparkMax flywheelMotor;
    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;

    private SparkMaxPIDController pidController;
    private RelativeEncoder encoder;
    public ShootingState state;
    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, kSetPoint;
    private double distanceToGoal;
    
    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        super("Shooter");
        
        this.flywheelMotor = flywheelMotor;
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;

        if(!Constants.SHOOTER_ENABLED) return;

        flywheelMotor.restoreFactoryDefaults();
        flywheelMotor.setIdleMode(IdleMode.kCoast);
        pidController = flywheelMotor.getPIDController();
        encoder = flywheelMotor.getEncoder();

        kP = 1e-4; 
        kI = 0;
        kD = 0; 
        kIz = 2; 
        kFF = 0.0002; 
        kMaxOutput = 1; 
        kMinOutput = -1;
        maxRPM = 5700;

        pidController.setP(kP);
        pidController.setI(kI);
        pidController.setD(kD);
        pidController.setIZone(kIz);
        pidController.setFF(kFF);
        pidController.setOutputRange(kMinOutput, kMaxOutput); 

        SmartDashboard.putNumber("P Gain", kP);
        SmartDashboard.putNumber("I Gain", kI);
        SmartDashboard.putNumber("D Gain", kD);
        SmartDashboard.putNumber("I Zone", kIz);
        SmartDashboard.putNumber("Feed Forward", kFF);
        SmartDashboard.putNumber("Max Output", kMaxOutput);
        SmartDashboard.putNumber("Min Output", kMinOutput);
        SmartDashboard.putNumber("RPM", 10);
    }

    public void setTurretAngle(double speed) {
        if(!Constants.SHOOTER_ENABLED) return;

        turretMotor.set(speed);
    }

    public void shoot() {
        if(!Constants.SHOOTER_ENABLED) return;

        double p = SmartDashboard.getNumber("P Gain", 0);
        double i = SmartDashboard.getNumber("I Gain", 0);
        double d = SmartDashboard.getNumber("D Gain", 0);
        double iz = SmartDashboard.getNumber("I Zone", 0);
        double ff = SmartDashboard.getNumber("Feed Forward", 0);
        double max = SmartDashboard.getNumber("Max Output", 0);
        double min = SmartDashboard.getNumber("Min Output", 0);
        double setPoint = SmartDashboard.getNumber("RPM", 0);

        if((p != kP)) { pidController.setP(p); kP = p; }
        if((i != kI)) { pidController.setI(i); kI = i; }
        if((d != kD)) { pidController.setD(d); kD = d; }
        if((iz != kIz)) { pidController.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { pidController.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) { 
            pidController.setOutputRange(min, max); 
            kMinOutput = min; kMaxOutput = max; 
        }
        if(setPoint != kSetPoint) {
            pidController.setReference(setPoint, ControlType.kVelocity);
            kSetPoint = setPoint;
            SmartDashboard.putNumber("SetPoint", setPoint);
        }

        SmartDashboard.putNumber("ProcessVariable", encoder.getVelocity());
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
