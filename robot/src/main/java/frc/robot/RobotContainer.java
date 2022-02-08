package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.subsystem.Climber;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);
    private final Solenoid leftNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
    private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
    private final CANSparkMax winchMotor = new CANSparkMax(Constants.WINCH_MOTOR_PORT, MotorType.kBrushless);
    private DigitalInput leftSwitchDigitalInput = new DigitalInput(0);
    private DigitalInput rightSwitchDigitalInput = new DigitalInput(1);
    private Climber climber = new Climber(leftNeutralHookActuator, climbingHookActuator, winchMotor, leftSwitchDigitalInput, rightSwitchDigitalInput);
    public RobotContainer() {
        climber.setDefaultCommand(new RunCommand(climber::idle, climber));
    }
}
