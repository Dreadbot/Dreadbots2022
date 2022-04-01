package frc.robot.util;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

public class DreadbotMotor{
    private CANSparkMax motor;
    private RelativeEncoder motorEncoder;
    private SparkMaxPIDController motorPIDController;
    private final String name;
    public boolean fullMotorInfo = false;
    private boolean isDisabled = false;

    public DreadbotMotor(CANSparkMax motor, String name){
        this.motor = motor;
        System.out.println(motor);
        this.motorEncoder = motor.getEncoder();
        System.out.println(motor.getEncoder());
        this.motorPIDController = motor.getPIDController();
        System.out.println(motor.getPIDController());
        this.name = name + " motor";
        System.out.println(name);
    }

    public void restoreFactoryDefaults(){
        if(isDisabled()) return;
        try{
            motor.restoreFactoryDefaults();
        } catch (RuntimeException ignored) {
            disable();
            printError("restoreFactoryDefaults");
        }
    }

    public void setIdleMode(IdleMode mode){
        if(isDisabled()) return;
        try{
            motor.setIdleMode(mode);
        } catch (RuntimeException ignored) {
            disable();
            printError("setIdleMode");
        }
    }

    public void setInverted(boolean isInverted){
        if(isDisabled()) return;
        try{
            motor.setInverted(isInverted);
        } catch (RuntimeException ignored) {
            disable();
            printError("setInverted");
        }
    }

    public REVLibError setPositionConversionFactor(Double factor){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setPositionConversionFactor(factor);
        } catch (RuntimeException ignored) {
            disable();
            printError("setPositionConversionFactor");
            return REVLibError.kError;
        }
    }

    public REVLibError setVelocityConversionFactor(double factor){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setVelocityConversionFactor(factor);
        } catch (RuntimeException ignored) {
            disable();
            printError("setVelocityConversionFactor");
            return REVLibError.kError;
        }
    }

    public double getVelocity(){
        if(isDisabled()) return -3656.0;
        try{
            return motorEncoder.getVelocity();
        } catch (RuntimeException ignored) {
            disable();
            printError("getVelocity");
            return -3656.0;
        }
    }

    public void setVoltage(double outputVolts){
        if(isDisabled()) return;
        try{
            motor.setVoltage(outputVolts);
        } catch (RuntimeException ignored) {
            disable();
            printError("setVoltage");
        }
    }

    public REVLibError setPosition(double position){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorEncoder.setPosition(position);
        } catch (RuntimeException ignored) {
            disable();
            printError("setPosition");
            return REVLibError.kError;
        }
    }

    public REVLibError resetEncoder(){
        if(isDisabled()) return REVLibError.kError;
        try{
            return setPosition(0.0d);
        } catch (RuntimeException ignored) {
            disable();
            printError("resetEncoder");
            return REVLibError.kError;
        }
    }

    public void close(){
        if(isDisabled()) return;
        try{
            motor.close();   
        } catch (IllegalStateException ignored) {
            disable();
            printError("close");
        }
    }

    public double get(){
        if(isDisabled()) return 0;
        try{
            return motor.get();   
        } catch (RuntimeException ignored) {
            disable();
            printError("get");
            return 0;
        }
    }

    public void set(double speed){
        System.out.println(isDisabled());
        if(isDisabled()) return;
        try{
            motor.set(speed);   
        } catch (RuntimeException ignored) {
            disable();
            printError("set");
        }
    }

    public void stopMotor(){
        if(isDisabled()) return;
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

    public SparkMaxPIDController getPIDController(){
        return motorPIDController;
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
    
    public REVLibError setP(double gain){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorPIDController.setP(gain);
        } catch (RuntimeException ignored) {
            disable();
            printError("setP");
            return REVLibError.kError;
        }
    }

    public double getP(){
        if(isDisabled()) return 0;
        try{
            return motorPIDController.getP();
        } catch (RuntimeException ignored) {
            disable();
            printError("getP");
            return 0;
        }
    }

    public REVLibError setI(double gain){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorPIDController.setI(gain);
        } catch (RuntimeException ignored) {
            disable();
            printError("setI");
            return REVLibError.kError;
        }
    }

    public double getI(){
        if(isDisabled()) return 0;
        try{
            return motorPIDController.getI();
        } catch (RuntimeException ignored) {
            disable();
            printError("getI");
            return 0;
        }
    }

    public REVLibError setD(double gain){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorPIDController.setD(gain);
        } catch (RuntimeException ignored) {
            disable();
            printError("setD");
            return REVLibError.kError;
        }
    }

    public double getD(){
        if(isDisabled()) return 0;
        try{
            return motorPIDController.getD();
        } catch (RuntimeException ignored) {
            disable();
            printError("getD");
            return 0;
        }
    }

    public REVLibError setIZone(double gain){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorPIDController.setIZone(gain);
        } catch (RuntimeException ignored) {
            disable();
            printError("setIZone");
            return REVLibError.kError;
        }
    }

    public double getIZone(){
        if(isDisabled()) return 0;
        try{
            return motorPIDController.getIZone();
        } catch (RuntimeException ignored) {
            disable();
            printError("getIZone");
            return 0;
        }
    }

    public REVLibError setFF(double gain){
        if(isDisabled()) return REVLibError.kError;
        try{
            return motorPIDController.setFF(gain);
        } catch (RuntimeException ignored) {
            disable();
            printError("setFF");
            return REVLibError.kError;
        }
    }

    public double getFF(){
        if(isDisabled()) return 0;
        try{
            return motorPIDController.getFF();
        } catch (RuntimeException ignored) {
            disable();
            printError("getFF");
            return 0;
        }
    }

    public void PIDTuner(){
        setP(SmartDashboard.getNumber(name + "P value", getP()));
        setI(SmartDashboard.getNumber(name + "I value", getI()));
        setD(SmartDashboard.getNumber(name + "D value", getD()));
        setIZone(SmartDashboard.getNumber(name + "I Zone value", getIZone()));
        setFF(SmartDashboard.getNumber(name + "FF value", getFF()));
    }
}