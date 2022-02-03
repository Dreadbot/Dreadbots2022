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
    private CANSparkMax externalIntakeMotor;
    private CANSparkMax internalIntakeMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        externalIntakeMotor = new CANSparkMax(Constants.LEFT_INTAKE_MOTOR_PORT, MotorType.kBrushless);
        internalIntakeMotor = new CANSparkMax(Constants.RIGHT_INTAKE_MOTOR_PORT, MotorType.kBrushless);

        intake = new Intake(externalIntakeMotor, internalIntakeMotor);
    }

    @After
    public void shutdown() throws Exception {
        externalIntakeMotor.close();
        internalIntakeMotor.close();
        intake.close();
    }

    @Test
    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.intake();

        assertEquals(1.0d, externalIntakeMotor.get(), DELTA);
        assertEquals(1.0d, internalIntakeMotor.get(), DELTA);
    }

    @Test
    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.outlet();

        assertEquals(-1.0d, externalIntakeMotor.get(), DELTA);
        assertEquals(-1.0d, internalIntakeMotor.get(), DELTA);
    }

    @Test
    public void intakeDisabled() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.disable();
        intake.intake();

        assertEquals(0.0d, externalIntakeMotor.get(), DELTA);
        assertEquals(0.0d, internalIntakeMotor.get(), DELTA);
    }

    @Test
    public void outletDisabled() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.disable();
        intake.outlet();

        assertEquals(0.0d, externalIntakeMotor.get(), DELTA);
        assertEquals(0.0d, internalIntakeMotor.get(), DELTA);
    }

    @Test
    public void stop() {
        if(!Constants.INTAKE_ENABLED) return;
        
        intake.stopMotors();

        assertEquals(0.0d, externalIntakeMotor.get(), DELTA);
        assertEquals(0.0d, internalIntakeMotor.get(), DELTA);
    }
}
