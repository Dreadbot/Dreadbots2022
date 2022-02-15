package frc.robot.subsystem.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.junit.After;
import org.junit.Before;
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
    }
    
    @After
    public void shutdown() throws Exception {
        flywheelMotor.close();
        hoodMotor.close();
        turretMotor.close();
    }
}
