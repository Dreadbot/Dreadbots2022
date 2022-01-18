package frc.robot.subsystem;

import static org.junit.Assert.assertEquals;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;

public class IntakeTest {
    public static final double DELTA = 1e-2;

    private Intake intake;
    private CANSparkMax leftIntakeMotor;
    private CANSparkMax rightIntakeMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        leftIntakeMotor = new CANSparkMax(Constants.LEFT_INTAKE_MOTOR_PORT, MotorType.kBrushless);
        rightIntakeMotor = new CANSparkMax(Constants.RIGHT_INTAKE_MOTOR_PORT, MotorType.kBrushless);

        intake = new Intake(leftIntakeMotor, rightIntakeMotor);
    }

    @After
    public void shutdown() throws Exception {
        leftIntakeMotor.close();
        rightIntakeMotor.close();
        intake.close();
    }

    @Test
    public void intake() {
        leftIntakeMotor.set(1.0d);
        rightIntakeMotor.set(1.0d);

        assertEquals(1.0d, leftIntakeMotor.get(), DELTA);
        assertEquals(1.0d, rightIntakeMotor.get(), DELTA);
    }

    @Test
    public void outlet() {
        leftIntakeMotor.set(-1.0d);
        rightIntakeMotor.set(-1.0d);

        assertEquals(-1.0d, leftIntakeMotor.get(), DELTA);
        assertEquals(-1.0d, rightIntakeMotor.get(), DELTA);
    }

    @Test
    public void stop() {
        leftIntakeMotor.set(0.0d);
        rightIntakeMotor.set(0.0d);

        assertEquals(0.0d, leftIntakeMotor.get(), DELTA);
        assertEquals(0.0d, rightIntakeMotor.get(), DELTA);
    }
}
