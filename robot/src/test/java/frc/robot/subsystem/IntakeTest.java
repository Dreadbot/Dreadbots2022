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
    private CANSparkMax intakeMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);

        intake = new Intake(intakeMotor);
    }

    @After
    public void shutdown() throws Exception {
        intakeMotor.close();
        intake.close();
    }

    @Test
    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.intake();

        assertEquals(1.0d, intakeMotor.get(), DELTA);
    }

    @Test
    public void outlet() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.outtake();

        assertEquals(-1.0d, intakeMotor.get(), DELTA);
    }

    @Test
    public void intakeDisabled() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.intake();
    }

    @Test
    public void outletDisabled() {
        if(!Constants.INTAKE_ENABLED) return;

        intake.outtake();

        assertEquals(-1.0d, intakeMotor.get(), DELTA);
    }

    @Test
    public void stop() {
        if(!Constants.INTAKE_ENABLED) return;
        
        intake.stopMotors();

        assertEquals(0.0d, intakeMotor.get(), DELTA);
    }
}
