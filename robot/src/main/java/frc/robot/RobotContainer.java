package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    @SuppressWarnings("unused")
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private CANSparkMax leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);

    private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);
    private Intake intake = new Intake(intakeMotor);

    private final CANSparkMax flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax hoodMotor = new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax turretMotor = new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless);

    private Shooter shooter = new Shooter(flywheelMotor, hoodMotor, turretMotor);

    private final Solenoid leftNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.LEFT_NEUTRAL_HOOK_ACTUATOR);
    private final Solenoid rightNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.RIGHT_NEUTRAL_HOOK_ACTUATOR);

    private final Solenoid leftClimbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.LEFT_CLIMBING_HOOK_ACTUATOR);
    private final Solenoid rightClimbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.RIGHT_CLIMBING_HOOK_ACTUATOR);

    private final CANSparkMax leftWinchMotor = new CANSparkMax(Constants.LEFT_WINCH_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax rightWinchMotor = new CANSparkMax(Constants.RIGHT_WINCH_MOTOR_PORT, MotorType.kBrushless);
    @SuppressWarnings("unused")
    private Climber climber = new Climber(leftNeutralHookActuator, rightNeutralHookActuator, leftClimbingHookActuator, rightClimbingHookActuator, leftWinchMotor, rightWinchMotor);
    
    public RobotContainer() {
        if(!Constants.DRIVE_ENABLED) {
            leftFrontDriveMotor.close();
            rightFrontDriveMotor.close();
            leftBackDriveMotor.close();
            rightBackDriveMotor.close();
        }
        
        if(!Constants.CLIMB_ENABLED) {
            leftNeutralHookActuator.close();
            rightNeutralHookActuator.close();
        
            leftClimbingHookActuator.close();
            rightClimbingHookActuator.close();
        
            leftWinchMotor.close();
            rightWinchMotor.close();
        }
    }

    public void periodic() {
        drive.driveCartesian(primaryController.getYAxis(), primaryController.getXAxis(), 0);

        if(secondaryController.isBButtonPressed())
            shooter.shoot();
        else 
            shooter.idle();

        if(secondaryController.isAButtonPressed()) 
            intake.intake();
        if(secondaryController.isXButtonPressed()) 
            intake.outlet();
        if(secondaryController.isAButtonPressed() == secondaryController.isXButtonPressed()) 
            intake.idle();
    }
}
