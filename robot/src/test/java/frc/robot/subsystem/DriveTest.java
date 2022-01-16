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
        leftFrontDriveMotor.set(1.0d);
        rightFrontDriveMotor.set(1.0d);
        leftBackDriveMotor.set(1.0d);
        rightBackDriveMotor.set(1.0d);

        assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
        assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
        assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
        assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
    }
}
