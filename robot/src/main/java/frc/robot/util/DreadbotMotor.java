package frc.robot.util;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.subsystem.Drive;

public class DreadbotMotor{
    private CANSparkMax motor;
    private RelativeEncoder motorEncoder;
    private final String name;
    public boolean fullInfo = false;

    public DreadbotMotor(CANSparkMax motor, String name){
        this.motor = motor;
        this.motorEncoder = motor.getEncoder();
        this.name = name;
    }

    public void restoreFactoryDefaults(){
        motor.restoreFactoryDefaults();
    }

    public void setIdleMode(IdleMode mode){
        motor.setIdleMode(mode);
    }

    public void setInverted(boolean isInverted){
        motor.setInverted(isInverted);
    }

    public REVLibError setPositionConversionFactor(Double factor){
        return motorEncoder.setPositionConversionFactor(factor);
    }

    public REVLibError setVelocityConversionFactor(double factor){
        return motorEncoder.setVelocityConversionFactor(factor);
    }

    public double getVelocity(){
        return motorEncoder.getVelocity();
    }

    public void setVoltage(double outputVolts){
        motor.setVoltage(outputVolts);
    }

    public REVLibError setPosition(double position){
        return motorEncoder.setPosition(position);
    }

    public REVLibError resetEncoder(){
        return setPosition(0.0d);
    }

    public void close(){
        motor.close();
    }

    public RelativeEncoder getEncoder(){
        return motorEncoder;
    }

    public CANSparkMax getSparkMax(){
        return motor;
    }

    public double get(){
        return motor.get();
    }
}