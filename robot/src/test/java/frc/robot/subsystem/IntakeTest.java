package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.hal.HAL;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.DreadbotMotor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;

import java.util.logging.Level;

import static org.junit.Assert.*;

public class IntakeTest {
    public static final double DELTA = 1e-2;

    private Intake intake;
    private DreadbotMotor intakeMotor;
    

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        // This logic notifies the programmer which systems are disabled in the Constants file.
        // The DreadbotSubsystem class will throw a warning while the log level is here.
        Robot.LOGGER.setLevel(Level.INFO);

        intakeMotor = new DreadbotMotor (new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless), "Intake");
        intake = new Intake(intakeMotor);

        // Set log level higher than warnings, so that tests do not log disabled warnings.
        Robot.LOGGER.setLevel(Level.SEVERE);
    }

    @After
    public void shutdown() {
        intake.close();

        // Return to the regular log level.
        Robot.LOGGER.setLevel(Level.INFO);
    }

    @Test
    public void intake() {
        intake.intake();

        if (intake.isEnabled()) {
            assertEquals(Constants.INTAKE_INTAKING_MAX_POWER, intakeMotor.get(), DELTA);
        }
    }

    @Test
    public void outtake() {
        intake.outtake();

        if (intake.isEnabled()) {
            assertEquals(Constants.INTAKE_OUTTAKING_MAX_POWER, intakeMotor.get(), DELTA);
        }
    }

    @Test
    public void idle() {
        intake.idle();

        if (intake.isEnabled()) {
            assertEquals(0.0d, intakeMotor.get(), DELTA);
        }
    }

    @Test
    public void stopMotors() {
        intake.intake();
        intake.stopMotors();

        if (intake.isEnabled()) {
            assertEquals(0.0d, intakeMotor.get(), DELTA);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void isIntaking() {
        intake.intake();
        if (intake.isEnabled()) {
            assertTrue(intake.isIntaking());
        } else {
            assertFalse(intake.isIntaking());
        }

        intake.idle();
        assertFalse(intake.isIntaking());
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void isOuttaking() {
        intake.outtake();
        if (intake.isEnabled()) {
            assertTrue(intake.isOuttaking());
        } else {
            assertFalse(intake.isOuttaking());
        }

        intake.idle();
        assertFalse(intake.isOuttaking());
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test(expected = None.class /* No exception should be thrown */)
    public void close() {
        intake.close();

        // Despite making calls to closed objects, these functions should not
        // throw an exception. This test case is another check to ensure calls
        // to closed motors do not crash the robot.
        intake.intake();
        intake.outtake();
        intake.stopMotors();
        intake.isIntaking();
        intake.isOuttaking();
        intake.close();
    }
}
