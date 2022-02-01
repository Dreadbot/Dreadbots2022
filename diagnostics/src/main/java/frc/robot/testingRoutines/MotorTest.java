package frc.robot.testingRoutines;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;

public class MotorTest {

    private CANSparkMax motor1; 
    private CANSparkMax motor2;
    private CANSparkMax motor3;

   public MotorTest (CANSparkMax motor1, CANSparkMax motor2, CANSparkMax motor3){
    this.motor1 = motor1;
    this.motor2 = motor2;
    this.motor3 = motor3;
   }

    public void testMotor1(double speed) {
        motor1.set(speed);
    }
    public void testMotor2(double speed) {
        motor2.set(speed);
    }
    public void testMotor3(double speed) {
        motor3.set(speed);
    }
    public void testMotor1() {
        SparkMaxPIDController CalibratePIDController = motor1.getPIDController();
        CalibratePIDController.setP(0);
    }
}