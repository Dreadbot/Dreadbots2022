package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;

import static org.junit.Assert.*;

public class IntakeTest {
    public static final double DELTA = 1e-2;

    private Intake intake;
    private CANSparkMax intakeMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);

        intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);
        intake = new Intake(intakeMotor);
    }

    @After
    public void shutdown() {
        intake.close();
    }

    @Test
    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.intake();

        assertEquals(1.0d, intakeMotor.get(), DELTA);
    }

    @Test
    public void outtake() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.outtake();

        assertEquals(-1.0d, intakeMotor.get(), DELTA);
    }

    @Test
    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.idle();

        assertEquals(0.0, intakeMotor.get(), DELTA);
    }

    @Test
    public void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.stopMotors();

        assertEquals(0.0d, intakeMotor.get(), DELTA);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void isIntaking() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.intake();
        assertTrue(intake.isIntaking());

        intake.idle();
        assertFalse(intake.isIntaking());
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void isOuttaking() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.outtake();
        assertTrue(intake.isOuttaking());

        intake.idle();
        assertFalse(intake.isOuttaking());
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test(expected = Test.None.class /* No exception should be thrown */)
    public void close() {
        if(!Constants.INTAKE_ENABLED) return;

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
