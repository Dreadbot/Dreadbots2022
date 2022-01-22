package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Shooter extends Subsystem {
    private final CANSparkMax flywheelMotor;
    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;

    private SparkMaxPIDController pidController;
    private RelativeEncoder encoder;

    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;
    private double distanceToGoal;

    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        super("Shooter");
        
        this.flywheelMotor = flywheelMotor;
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;

        flywheelMotor.restoreFactoryDefaults();
        pidController = flywheelMotor.getPIDController();
        encoder = flywheelMotor.getEncoder();

        kP = 6e-5; 
        kI = 0;
        kD = 0; 
        kIz = 0; 
        kFF = 0.000015; 
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

    public void setTurretAngle(double turretAngle) {
        //TODO
    }

    public void shoot() {
        double p = SmartDashboard.getNumber("P Gain", 0);
        double i = SmartDashboard.getNumber("I Gain", 0);
        double d = SmartDashboard.getNumber("D Gain", 0);
        double iz = SmartDashboard.getNumber("I Zone", 0);
        double ff = SmartDashboard.getNumber("Feed Forward", 0);
        double max = SmartDashboard.getNumber("Max Output", 0);
        double min = SmartDashboard.getNumber("Min Output", 0);

        System.out.println("kP: " + kP);

        if((p != kP)) { pidController.setP(p); kP = p; }
        if((i != kI)) { pidController.setI(i); kI = i; }
        if((d != kD)) { pidController.setD(d); kD = d; }
        if((iz != kIz)) { pidController.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { pidController.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) { 
            pidController.setOutputRange(min, max); 
            kMinOutput = min; kMaxOutput = max; 
        }

        double setPoint = SmartDashboard.getNumber("RPM", 0) / 2;
        System.out.println("setpoint: " + setPoint);

        pidController.setReference(setPoint, ControlType.kVelocity);

        SmartDashboard.putNumber("SetPoint", setPoint);
        SmartDashboard.putNumber("ProcessVariable", encoder.getVelocity());
    }

    @SuppressWarnings("unused")
    private void setHoodPosition(double hoodAngle) {
        //TODO
    }

    @SuppressWarnings("unused")
    private void setFlywheelRPM(double revolutionsPerMinute) {
        //TODO
    }
    public double getRequiredFlyWheelRPM() {
        return Constants.BASE_RPM * Math.sqrt(Math.pow(calculateBVoy(), 2) + Math.pow(distanceToGoal / calculateTScore(), 2));
    }
    public double getRequiredHoodAngle() {
        return Math.atan(calculateBVoy() / (distanceToGoal / calculateTScore())) * 180 / Math.PI;
    }

    @Override
    public void close() throws Exception {
        flywheelMotor.close();
        hoodMotor.close();
        turretMotor.close();
    }
    @Override
    protected void stopMotors() {
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
        return ((0.5f * distanceToGoal) + 2.5f);
    }
    public double calculateBVoy() {
        return Math.sqrt(-2 * Constants.GRAVITY * (calculateArcHeight() - Constants.INITIAL_BALL_HEIGHT));
    }
    public double calculateTScore() {
        double bVoy = calculateBVoy();
        return (-bVoy - Math.sqrt(Math.pow(bVoy, 2) - 2 * Constants.GRAVITY * (Constants.INITIAL_BALL_HEIGHT - Constants.GOAL_HEIGHT))) / Constants.GRAVITY;
    }
}
