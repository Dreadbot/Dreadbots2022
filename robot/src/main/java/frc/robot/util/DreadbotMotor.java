package frc.robot.util;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.Robot;
import frc.robot.subsystem.Drive;

public class DreadbotMotor{
    private CANSparkMax motor;
    private RelativeEncoder motorEncoder;
    private final String name;
    public boolean fullMotorInfo = false;
    private boolean isDisabled = false;

    public DreadbotMotor(CANSparkMax motor, String name){
        this.motor = motor;
        this.motorEncoder = motor.getEncoder();
        this.name = name + " motor";
    }

    public void restoreFactoryDefaults(){
        if(!isDisabled()) return;
        try{
            motor.restoreFactoryDefaults();
        } catch (RuntimeException ignored) {
            disable();
            printError("restoreFactoryDefaults");
        }
    }

    public void setIdleMode(IdleMode mode){
        if(!isDisabled()) return;
        try{
            motor.setIdleMode(mode);
        } catch (RuntimeException ignored) {
            disable();
            printError("setIdleMode");
        }
    }

    public void setInverted(boolean isInverted){
        if(!isDisabled()) return;
        try{
            motor.setInverted(isInverted);
        } catch (RuntimeException ignored) {
            disable();
            printError("setInverted");
        }
    }

    public REVLibError setPositionConversionFactor(Double factor){
        if(!isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setPositionConversionFactor(factor);
        } catch (RuntimeException ignored) {
            disable();
            printError("setPositionConversionFactor");
            return REVLibError.kError;
        }
    }

    public REVLibError setVelocityConversionFactor(double factor){
        if(!isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setVelocityConversionFactor(factor);
        } catch (RuntimeException ignored) {
            disable();
            printError("setVelocityConversionFactor");
            return REVLibError.kError;
        }
    }

    public double getVelocity(){
        if(!isDisabled()) return -3656.0;
        try{
            return motorEncoder.getVelocity();
        } catch (RuntimeException ignored) {
            disable();
            printError("getVelocity");
            return -3656.0;
        }
    }

    public void setVoltage(double outputVolts){
        if(!isDisabled()) return;
        try{
            motor.setVoltage(outputVolts);
        } catch (RuntimeException ignored) {
            disable();
            printError("setVoltage");
        }
    }

    public void setVoltage(PIDController velocityController, double currentVelocity){
        if(!isDisabled()) return;
        try{
            final double rawVoltage = Drive.FEEDFORWARD.calculate(velocityController.getSetpoint())
                + velocityController.calculate(currentVelocity);
            motor.setVoltage(DreadbotMath.clampValue(rawVoltage, -12.0, 12.0));   
        } catch (RuntimeException ignored) {
            disable();
            printError("setVoltage");
        }
    }

    public REVLibError setPosition(double position){
        if(!isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setPosition(position);
        } catch (RuntimeException ignored) {
            disable();
            printError("setPosition");
            return REVLibError.kError;
        }
    }

    public REVLibError resetEncoder(){
        if(!isDisabled()) return REVLibError.kError;
        try{
            return setPosition(0.0d);
        } catch (RuntimeException ignored) {
            disable();
            printError("resetEncoder");
            return REVLibError.kError;
        }
    }

    public void close(){
        if(!isDisabled()) return;
        try{
            motor.close();   
        } catch (RuntimeException ignored) {
            disable();
            printError("close");
        }
    }

    public double get(){
        if(!isDisabled()) return -3656.0;
        try{
            return motor.get();   
        } catch (RuntimeException ignored) {
            disable();
            printError("get");
            return -3656.0;
        }
    }

    public void set(double speed){
        if(!isDisabled()) return;
        try{
            motor.set(speed);   
        } catch (RuntimeException ignored) {
            disable();
            printError("set");
        }
    }

    public void stopMotor(){
        if(!isDisabled()) return;
        try{
            motor.stopMotor(); 
        } catch (RuntimeException ignored) {
            disable();
            printError("close");
        }
    }

    public RelativeEncoder getEncoder(){
        return motorEncoder;
    }

    public CANSparkMax getSparkMax(){
        return motor;
    }

    public void disable(){
        isDisabled = true;
    }

    public boolean isDisabled(){
        return isDisabled;
    }

    private void printError(String errorTrace){
        Robot.LOGGER.warning(name + "was disabled by " + errorTrace + "()");
    }
}