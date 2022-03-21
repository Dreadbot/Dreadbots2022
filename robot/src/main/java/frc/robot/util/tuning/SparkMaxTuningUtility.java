package frc.robot.util.tuning;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class SparkMaxTuningUtility implements Sendable {
    private CANSparkMax motorController;

    private double kP, kI, kD, kFF, kIZone;
    private double setpoint;

    public SparkMaxTuningUtility(CANSparkMax motorController) throws IllegalStateException {
        this.motorController = motorController;

        this.kP = motorController.getPIDController().getP();
        this.kI = motorController.getPIDController().getI();
        this.kD = motorController.getPIDController().getD();
        this.kFF = motorController.getPIDController().getFF();
        this.kIZone = motorController.getPIDController().getIZone();
    }

    /**
     * Initializes this {@link Sendable} object.
     *
     * @param builder sendable builder
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SparkMaxTuningUtility");

        builder.addDoubleProperty("P", this::getkP, this::setkP);
        builder.addDoubleProperty("I", this::getkI, this::setkI);
        builder.addDoubleProperty("D", this::getkD, this::setkD);
        builder.addDoubleProperty("FF", this::getkFF, this::setkFF);
        builder.addDoubleProperty("IZone", this::getkIZone, this::setkIZone);
        builder.addDoubleProperty("SetPoint", this::getSetpoint, this::setSetpoint);
    }

    public void tune() {
        motorController.getPIDController().setReference(setpoint, CANSparkMax.ControlType.kVelocity);
    }

    public void stop() {
        motorController.stopMotor();
    }

    public double getkP() {
        return kP;
    }

    public void setkP(double kP) {
        if(this.kP == kP) return;

        this.kP = kP;
        this.motorController.getPIDController().setP(kP);
    }

    public double getkI() {
        return kI;
    }

    public void setkI(double kI) {
        if(this.kI == kI) return;

        this.kI = kI;
        this.motorController.getPIDController().setI(kI);
    }

    public double getkD() {
        return kD;
    }

    public void setkD(double kD) {
        if(this.kD == kD) return;

        this.kD = kD;
        this.motorController.getPIDController().setD(kD);
    }

    public double getkFF() {
        return kFF;
    }

    public void setkFF(double kFF) {
        if(this.kFF == kFF) return;

        this.kFF = kFF;
        this.motorController.getPIDController().setFF(kFF);
    }

    public double getkIZone() {
        return kIZone;
    }

    public void setkIZone(double kIZone) {
        if(this.kIZone == kIZone) return;

        this.kIZone = kIZone;
        this.motorController.getPIDController().setIZone(kIZone);
    }

    public double getSetpoint() {
        return setpoint;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }
}
