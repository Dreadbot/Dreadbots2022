package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import frc.robot.Constants;

public class Shooter implements AutoCloseable {
    private final CANSparkMax flywheelMotor;
    private final CANSparkMax hoodMotor;
    private final CANSparkMax turretMotor;
    private double distanceToGoal = 9.2f;
    public Shooter(CANSparkMax flywheelMotor, CANSparkMax hoodMotor, CANSparkMax turretMotor) {
        this.flywheelMotor = flywheelMotor;
        this.hoodMotor = hoodMotor;
        this.turretMotor = turretMotor;
    }

    public void setTurretAngle(double turretAngle) {
        //TODO
    }

    public void shoot() {
        //TODO
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
        return Constants.BASE_RPM * Math.sqrt(Math.pow(calculateBVoy(), 2) + distanceToGoal / calculateTScore());
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
    @SuppressWarnings("unused")
    private double calculateBVoy() {
        return Math.sqrt(-2 * Constants.GRAVITY * (calculateArcHeight() - Constants.INITIAL_BALL_HEIGHT));
    }
    @SuppressWarnings("unused")
    private double calculateTScore() {
        double bVoy = calculateBVoy();
        return (-bVoy - Math.sqrt(Math.pow(bVoy, 2) - 2 * Constants.GRAVITY * (Constants.INITIAL_BALL_HEIGHT - Constants.GOAL_HEIGHT))) / Constants.GRAVITY;
    }
}
