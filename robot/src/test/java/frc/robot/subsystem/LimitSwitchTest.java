package frc.robot.subsystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitchTest {
    public static final double DELTA = 1e-2;
    public DigitalInput leftSwitch;
    public DigitalInput rightSwitch;

    @SuppressWarnings("unused")
    @Before
    public void setup() {
        leftSwitch = new DigitalInput(1);
        rightSwitch = new DigitalInput(2);
    }
    
    @After
    public void shutdown() throws Exception {
        leftSwitch.close();
        rightSwitch.close();
    }
    public boolean getLeftLimitSwitch() {
        return !leftSwitch.get();
    }

    public boolean getRightLimitSwitch() {
        return !rightSwitch.get();
    }
}
