package frc.robot.subsystem;

import static org.junit.Assert.assertEquals;

import com.revrobotics.CANSparkMax;
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

    private double angle;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        // leftFrontDriveMotor = new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        // rightFrontDriveMotor = new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        // leftBackDriveMotor = new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);
        // rightBackDriveMotor = new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless);

        // rightFrontDriveMotor.setInverted(true);
        // rightBackDriveMotor.setInverted(true);

        // drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

        // angle = 0.0d;
    }

    @After
    public void shutdown() throws Exception {
        if(!Constants.DRIVE_ENABLED) return;
        leftFrontDriveMotor.close();
        rightFrontDriveMotor.close();
        leftBackDriveMotor.close();
        rightBackDriveMotor.close();
        drive.close();
    }

    @Test
    public void testJoystickPolarMath() {
        if(!Constants.DRIVE_ENABLED) return;
        // First Quadrant of the Joystick (-,+)
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 3), Math.cos(Math.PI / 3));
        assertEquals(-30.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 4), Math.cos(Math.PI / 4));
        assertEquals(-45.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 6), Math.cos(Math.PI / 6));
        assertEquals(-60.0d, angle, DELTA);

        // Second Quadrant of the Joystick (-,-)
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 3), -Math.cos(Math.PI / 3));
        assertEquals(30.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 4), -Math.cos(Math.PI / 4));
        assertEquals(45.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            -Math.sin(Math.PI / 6), -Math.cos(Math.PI / 6));
        assertEquals(60.0d, angle, DELTA);

        // Third Quadrant of the Joystick (+,-)
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 3), -Math.cos(Math.PI / 3));
        assertEquals(150.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 4), -Math.cos(Math.PI / 4));
        assertEquals(135.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 6), -Math.cos(Math.PI / 6));
        assertEquals(120.0d, angle, DELTA);
        
        // Fourth Quadrant of the Joystick (+, +)
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 3), Math.cos(Math.PI / 3));
        assertEquals(-150.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 4), Math.cos(Math.PI / 4));
        assertEquals(-135.0d, angle, DELTA);
        angle = Drive.getAngleDegreesFromJoystick(
            Math.sin(Math.PI / 6), Math.cos(Math.PI / 6));
        assertEquals(-120.0d, angle, DELTA);
    }

    @Test
    public void fullForwardDisabled() {
        if(!Constants.DRIVE_ENABLED) return;
        drive.drivePolar(1.0d, 0.0d, 0.0d);

        assertEquals(0.0d, leftFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, leftBackDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightBackDriveMotor.get(), DELTA);
    }

    @Test
    public void stopMotors() {
        if(!Constants.DRIVE_ENABLED) return;
        drive.stopMotors();

        assertEquals(0.0d, leftFrontDriveMotor.get(), 0.0d);
        assertEquals(0.0d, rightFrontDriveMotor.get(), 0.0d);
        assertEquals(0.0d, leftBackDriveMotor.get(), 0.0d);
        assertEquals(0.0d, rightBackDriveMotor.get(), 0.0d);
    }
}
