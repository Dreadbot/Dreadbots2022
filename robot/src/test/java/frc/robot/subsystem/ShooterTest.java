package frc.robot.subsystem;

import static org.junit.Assert.assertEquals;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.hal.HAL;
import frc.robot.Constants;

public class ShooterTest {
    public static final double DELTA = 1e-2;

    @SuppressWarnings("unused")
    private Shooter shooter;
    private CANSparkMax flywheelMotor;
    private CANSparkMax hoodMotor;
    private CANSparkMax turretMotor;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        flywheelMotor = new CANSparkMax(Constants.FLYWHEEL_MOTOR_PORT, MotorType.kBrushless);
        hoodMotor = new CANSparkMax(Constants.HOOD_MOTOR_PORT, MotorType.kBrushless);
        turretMotor = new CANSparkMax(Constants.TURRET_MOTOR_PORT, MotorType.kBrushless);

        shooter = new Shooter(flywheelMotor, hoodMotor, turretMotor);
    }
    @Test
    public void testMath() {
        assertEquals(9.50010d, shooter.calculateBVoy(), DELTA);
        assertEquals(1.66205d, shooter.calculateTScore(), DELTA);
        assertEquals(4286.0387d, shooter.getRequiredFlyWheelRPM(), DELTA);
        assertEquals(72.4289d, shooter.getRequiredHoodAngle(), DELTA);
    }
    @After
    public void shutdown() throws Exception {
        flywheelMotor.close();
        hoodMotor.close();
        turretMotor.close();
    }
}
