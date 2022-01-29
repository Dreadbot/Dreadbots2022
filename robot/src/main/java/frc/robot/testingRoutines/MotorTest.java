package frc.robot.testingRoutines;

import com.revrobotics.CANSparkMax;

public class MotorTest {

    private CANSparkMax motor1; 
    private CANSparkMax motor2;
    private CANSparkMax motor3;

   public MotorTest (CANSparkMax motor1, CANSparkMax motor2, CANSparkMax motor3){
    this.motor1 = motor1;
    this.motor2 = motor2;
    this.motor3 = motor3;
   }
}
