package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.command.drive.DriveCommand;
import frc.robot.command.intake.IntakeCommand;
import frc.robot.command.intake.OuttakeCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    @SuppressWarnings("unused")
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private CANSparkMax leftFrontDriveMotor = new CANSparkMax(1, MotorType.kBrushless);
    private CANSparkMax rightFrontDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
    private CANSparkMax leftBackDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
    private CANSparkMax rightBackDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
    private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private CANSparkMax intakeMotor = new CANSparkMax(5, MotorType.kBrushless);
    private Intake intake = new Intake(intakeMotor);

    private final CANSparkMax flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax hoodMotor = new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax turretMotor = new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless);
    private Shooter shooter = new Shooter(flywheelMotor, hoodMotor, turretMotor);

    private final Solenoid leftNeutralHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
    private final Solenoid climbingHookActuator = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
    private final CANSparkMax winchMotor = new CANSparkMax(Constants.WINCH_MOTOR_PORT, MotorType.kBrushless);

    private Climber climber = new Climber(leftNeutralHookActuator, climbingHookActuator, winchMotor);
    
    public RobotContainer() {    
        intake.setDefaultCommand(new RunCommand(intake::idle, intake));
        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));

        drive.setDefaultCommand(new DriveCommand(drive, 
            primaryController::getYAxis, 
            primaryController::getXAxis,
            primaryController::getZAxis));
    }

    public void periodic() {
        if(secondaryController.isBButtonPressed())
            shooter.shoot();
        else 
            shooter.idle();
        
        if(primaryController.isRightTriggerPressed()) {
            climber.extendArm();
        }
        if(primaryController.isRightBumperPressed()) {
            climber.halfExtendArm();
        }
        if(primaryController.isLeftTriggerPressed()){
            climber.retractArm();
        }
        
        if(primaryController.isAButtonPressed())
            climber.rotateNeutralHooksVertical();
        if(primaryController.isBButtonPressed())
            climber.rotateNeutralHooksDown();
        if(primaryController.isXButtonPressed())
            climber.rotateClimbingHookVertical();
        if(primaryController.isYButtonPressed())
            climber.rotateClimbingHookDown();
    }
}
