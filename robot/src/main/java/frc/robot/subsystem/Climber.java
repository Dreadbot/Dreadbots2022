package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.util.DreadbotController;

public class Climber {
    private final Solenoid shortHookSolenoid;
    private final Solenoid longHookSolenoid;
    private final CANSparkMax leftMotor;
    private final CANSparkMax rightMotor;
    private final DoubleSolenoid shortHookDSolenoid;
    private final DoubleSolenoid longHookDSolenoid;
    private final DreadbotController secondaryController;

    public Climber( Solenoid shortHookSolenoid, Solenoid longHookSolenoid,
     CANSparkMax leftMotor, CANSparkMax rightMotor,
     DoubleSolenoid shortHookDSolenoid, DoubleSolenoid longHookDSolenoid,
     DreadbotController secondaryController){
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.shortHookSolenoid = shortHookSolenoid;
        this.longHookSolenoid = longHookSolenoid;
        this.shortHookDSolenoid = shortHookDSolenoid;
        this.longHookDSolenoid = longHookDSolenoid;
        shortHookDSolenoid.set(Value.kForward);
        longHookDSolenoid.set(Value.kForward);
        this.secondaryController = secondaryController;
    }

    public void setShortHook()
    {
         if(secondaryController.isAButtonPressed())
         {
            shortHookSolenoid.set(true);
         }
         else if (secondaryController.isBButtonPressed())
         {
            shortHookSolenoid.set(false);
         }
    }

    public void setLongHook()
    {
        if(secondaryController.isXButtonPressed())
        {
            longHookSolenoid.set(true);
        }
        else if(secondaryController.isYButtonPressed())
        {
            longHookSolenoid.set(false);
        }
    }

    public void toggleDoubleHooks()
    {
        if(secondaryController.isAButtonPressed())
        {
            shortHookDSolenoid.toggle();   
        }
        
        if(secondaryController.isBButtonPressed())
        {
            longHookDSolenoid.toggle();
        }
    }
}
