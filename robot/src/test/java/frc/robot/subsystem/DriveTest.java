package frc.robot.subsystem;

import static org.junit.Assert.assertEquals;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;

public class DriveTest {
    public static final double DELTA = 1e-2;
    
    private Drive drive;
    private CANSparkMax leftFrontDriveMotor;
    private CANSparkMax rightFrontDriveMotor;
    private CANSparkMax leftBackDriveMotor;
    private CANSparkMax rightBackDriveMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);

        rightFrontDriveMotor.setInverted(true);
        rightBackDriveMotor.setInverted(true);

        drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);
    }

    @After
    public void shutdown() throws Exception {
        leftFrontDriveMotor.close();
        rightFrontDriveMotor.close();
        leftBackDriveMotor.close();
        rightBackDriveMotor.close();
        drive.close();
    }

    @Test
    public void fullForward() {
        drive.drive();

        assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
        assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
        assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
        assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
    }

    @Test
    public void fullForwardDisabled() {
        drive.disable();
        drive.drive();

        assertEquals(0.0d, leftFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, leftBackDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightBackDriveMotor.get(), DELTA);
    }

    @Test
    public void stopMotors() {
        drive.stopMotors();

        assertEquals(0.0d, leftFrontDriveMotor.get(), 0.0d);
        assertEquals(0.0d, rightFrontDriveMotor.get(), 0.0d);
        assertEquals(0.0d, leftBackDriveMotor.get(), 0.0d);
        assertEquals(0.0d, rightBackDriveMotor.get(), 0.0d);
    }
}
