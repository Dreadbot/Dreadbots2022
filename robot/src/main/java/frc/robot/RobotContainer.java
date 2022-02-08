package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.command.IntakeCommand;
import frc.robot.command.IntakeDefaultCommand;
import frc.robot.command.OuttakeCommand;
import frc.robot.subsystem.Climber;
import frc.robot.subsystem.Drive;
import frc.robot.subsystem.Intake;
import frc.robot.subsystem.shooter.Shooter;
import frc.robot.util.DreadbotController;

public class RobotContainer {
    private DreadbotController primaryController = new DreadbotController(Constants.PRIMARY_JOYSTICK_PORT);
    @SuppressWarnings("unused")
    private DreadbotController secondaryController = new DreadbotController(Constants.SECONDARY_JOYSTICK_PORT);

    private CANSparkMax leftFrontDriveMotor = new CANSparkMax(10, MotorType.kBrushless);
    private CANSparkMax rightFrontDriveMotor = new CANSparkMax(6, MotorType.kBrushless);
    private CANSparkMax leftBackDriveMotor = new CANSparkMax(8, MotorType.kBrushless);
    private CANSparkMax rightBackDriveMotor = new CANSparkMax(5, MotorType.kBrushless);
    private Drive drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

    private CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);
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
        if(!Constants.DRIVE_ENABLED) {
            leftFrontDriveMotor.close();
            rightFrontDriveMotor.close();
            leftBackDriveMotor.close();
            rightBackDriveMotor.close();
        }
        
        if(!Constants.CLIMB_ENABLED) {
            leftNeutralHookActuator.close();
      
            winchMotor.close();
        }

        intake.setDefaultCommand(new IntakeDefaultCommand(intake));
        secondaryController.getAButton().whileHeld(new OuttakeCommand(intake));
        secondaryController.getXButton().whileHeld(new IntakeCommand(intake));
        
    }

    public void periodic() {
        drive.driveCartesian(primaryController.getYAxis(), primaryController.getXAxis(), primaryController.getZAxis());

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
