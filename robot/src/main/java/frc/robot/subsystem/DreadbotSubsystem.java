package frc.robot.subsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MotorSafeSystem;

public abstract class DreadbotSubsystem extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    private boolean enabled = true;

    protected DreadbotSubsystem() { }

    public void disable() {
        this.enabled = false;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
