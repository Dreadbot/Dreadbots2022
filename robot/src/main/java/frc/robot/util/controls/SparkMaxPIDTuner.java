package frc.robot.util.controls;

import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class SparkMaxPIDTuner implements Sendable {
    private double p = 0.0d;
    private double i = 0.0d;
    private double d = 0.0d;
    private double ff = 0.0d;
    private double iZone = 0.0d;

    /**
     * Updates the values in the controller infrequently.
     *
     * @param controller The SparkMaxPIDController to update.
     */
    public void tune(SparkMaxPIDController controller) {
        if(p != controller.getP()) controller.setP(p);
        if(i != controller.getI()) controller.setI(i);
        if(d != controller.getD()) controller.setD(d);
        if(ff != controller.getFF()) controller.setFF(p);
        if(iZone != controller.getIZone()) controller.setIZone(p);
    }

    /**
     * Initializes this {@link Sendable} object.
     *
     * @param builder sendable builder
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SparkMaxPIDTuner");

        builder.addDoubleProperty("P-Value", this::getP, this::setP);
        builder.addDoubleProperty("I-Value", this::getI, this::setI);
        builder.addDoubleProperty("D-Value", this::getD, this::setD);
        builder.addDoubleProperty("FF-Value", this::getFF, this::setFF);
        builder.addDoubleProperty("IZone-Value", this::getIZone, this::setIZone);
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
        this.i = i;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getFF() {
        return ff;
    }

    public void setFF(double ff) {
        this.ff = ff;
    }

    public double getIZone() {
        return iZone;
    }

    public void setIZone(double iZone) {
        this.iZone = iZone;
    }
}
