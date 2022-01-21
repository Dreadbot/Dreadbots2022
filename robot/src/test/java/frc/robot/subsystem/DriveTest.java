package frc.robot.subsystem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import org.mockito.Mockito;

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
    public void fullForwardDisabled() {
        drive.disable();
        drive.drive(1.0d, 0.0d, 0.0d);
        
        assertEquals(0.0d, leftFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightFrontDriveMotor.get(), DELTA);
        assertEquals(0.0d, leftBackDriveMotor.get(), DELTA);
        assertEquals(0.0d, rightBackDriveMotor.get(), DELTA);
    }

    @Test
    public void testGetAngle() {
        double x = 0.5;
        double y = -0.5;
        // forward to the right
        assertEquals(-45.0, drive.getAngle(y, x), DELTA);

        x = 0;
        y = -1.0;
        // straight forward
        assertEquals(-0.0, drive.getAngle(y, x), DELTA);

        x = -.5;
        y = -.5;
        // forward to the left
        assertEquals(45.0, drive.getAngle(y, x), DELTA);

        x = -1;
        y = 0;
        // straight left
        assertEquals(90.0, drive.getAngle(y, x), DELTA);

        x = -0.5;
        y = 0.5;
        // back and to the left
        assertEquals(135.0, drive.getAngle(y, x), DELTA);

        x = 0;
        y = 1;
        // straight backwards
        // positive or negative 180 is correct, so get absolute value of the angle
        assertEquals(180.0, Math.abs(drive.getAngle(y, x)), DELTA);

        x = 0.5;
        y = 0.5;
        // back and to the right
        assertEquals(-135.0, drive.getAngle(y, x), DELTA);

        x = 1;
        y = 0.0;
        // straight right
        assertEquals(-90.0, drive.getAngle(y, x), DELTA);
    }

    @Test
    public void testStopMotors() {
        // create mock (fake) motor objects - could be done in setup()
        CANSparkMax leftFrontMockMotor = Mockito.mock(CANSparkMax.class);
        CANSparkMax rightFrontMockMotor = Mockito.mock(CANSparkMax.class);
        CANSparkMax leftBackMockMotor = Mockito.mock(CANSparkMax.class);
        CANSparkMax rightBackMockMotor = Mockito.mock(CANSparkMax.class);

        // create drive object with mock motors
        Drive drive = new Drive(leftFrontMockMotor, rightFrontMockMotor, leftBackMockMotor, rightBackMockMotor);
        // call method to be tested
        drive.stopMotors();

        /*
            We expect stopMotors() to result in stopMotor() being called on each of the 4 drive motors
            While it would not cause any issues to call stopMotor() more than once, it isn't necessary and
            decreases performance, so we hope that stopMotor() is called only once on each motor object.

            Mockito.verify() reference:
            https://www.logicbig.com/tutorials/unit-testing/mockito/verifying-varing-number-of-invocations.html
         */

        // Verify stopMotor was called at Least Once...
        verify(leftFrontMockMotor, atLeastOnce()).stopMotor();
        // Verify stopMotor was called exactly Once...
        verify(rightFrontMockMotor, times(1)).stopMotor();
        // Verify stopMotor was called exactly Once, shorthand (times is optional)
        verify(leftBackMockMotor).stopMotor();
        // Verify stopMotor was called at most twice
        verify(rightBackMockMotor, atMost(2)).stopMotor();

        // Verify "set" isn't called at all with any value --not needed for this test, but wanted an example
        verify(rightBackMockMotor, never()).set(anyDouble());
    }
}
