package frc.robot.subsystem;

import static org.junit.Assert.assertEquals;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.Robot;
import frc.robot.util.DreadbotMotor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;

import java.util.logging.Level;

public class DriveTest {
    public static final double DELTA = 1e-2;
    
    private Drive drive;
    private DreadbotMotor leftFrontDriveMotor;
    private DreadbotMotor rightFrontDriveMotor;
    private DreadbotMotor leftBackDriveMotor;
    private DreadbotMotor rightBackDriveMotor;

    private double angle;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        // This logic notifies the programmer which systems are disabled in the Constants file.
        // The DreadbotSubsystem class will throw a warning while the log level is here.
        Robot.LOGGER.setLevel(Level.INFO);

        leftFrontDriveMotor = new DreadbotMotor( new CANSparkMax(Constants.LEFT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Left Front Drive");
        rightFrontDriveMotor = new DreadbotMotor( new CANSparkMax(Constants.RIGHT_FRONT_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Right Front Drive");
        leftBackDriveMotor = new DreadbotMotor( new CANSparkMax(Constants.LEFT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Left Back Drive");
        rightBackDriveMotor = new DreadbotMotor( new CANSparkMax(Constants.RIGHT_BACK_DRIVE_MOTOR_PORT, MotorType.kBrushless), "Right Back Drive");

        drive = new Drive(leftFrontDriveMotor, rightFrontDriveMotor, leftBackDriveMotor, rightBackDriveMotor);

        angle = 0.0d;

        // Set log level higher than warnings, so that tests do not log disabled warnings.
        Robot.LOGGER.setLevel(Level.SEVERE);
    }

    @After
    public void shutdown() {
        drive.close();

        // Return to the regular log level.
        Robot.LOGGER.setLevel(Level.INFO);
    }

    @Test
    public void getAngleDegreesFromJoystick() {
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
    public void driveCartesian() {
        // Full forward
        drive.driveCartesian(1.0d, 0.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full backward
        drive.driveCartesian(-1.0d, 0.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full strafe left
        drive.driveCartesian(0.0d, -1.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full strafe right
        drive.driveCartesian(0.0d, 1.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full negative rotation
        drive.driveCartesian(0.0d, 0.0d, -1.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full positive rotation
        drive.driveCartesian(0.0d, 0.0d, 1.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }
    }

    @Test
    public void drivePolar() {
        // Full forward
        drive.drivePolar(1.0d, 0.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full backward
        drive.drivePolar(1.0d, 180.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full strafe left
        drive.drivePolar(1.0d, -90.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full strafe right
        drive.drivePolar(1.0d, 90.0d, 0.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full negative rotation
        drive.drivePolar(0.0d, 0.0d, -1.0d);
        if (drive.isEnabled()) {
            assertEquals(-1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(1.0d, rightBackDriveMotor.get(), DELTA);
        }

        // Full positive rotation
        drive.drivePolar(0.0d, 0.0d, 1.0d);
        if (drive.isEnabled()) {
            assertEquals(1.0d, leftFrontDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightFrontDriveMotor.get(), DELTA);
            assertEquals(1.0d, leftBackDriveMotor.get(), DELTA);
            assertEquals(-1.0d, rightBackDriveMotor.get(), DELTA);
        }
    }

    @Test
    public void stopMotors() {
        drive.stopMotors();

        if (drive.isEnabled()) {
            assertEquals(0.0d, leftFrontDriveMotor.get(), 0.0d);
            assertEquals(0.0d, rightFrontDriveMotor.get(), 0.0d);
            assertEquals(0.0d, leftBackDriveMotor.get(), 0.0d);
            assertEquals(0.0d, rightBackDriveMotor.get(), 0.0d);
        }
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test(expected = None.class /* No exception should be thrown */)
    public void close() {
        drive.close();

        // Despite making calls to closed objects, these functions should not
        // throw an exception. This test case is another check to ensure calls
        // to closed motors do not crash the robot.
        drive.driveCartesian(1.0d, -1.0d, 0.0d);
        drive.drivePolar(1.0d, -90.0d, 23.0d);
        drive.stopMotors();
        drive.close();
    }
}
