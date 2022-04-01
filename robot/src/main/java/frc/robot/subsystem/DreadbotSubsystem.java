package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.util.safety.MotorSafeSystem;

/**
 * Takes all the functionality for Command Subsystems, Closeable Systems, and Motor Safety
 * into one base class. Also adds a readable value {@link DreadbotSubsystem#enabled} to
 * determine if internal hardware is failing. To read the value externally, use
 * {@link DreadbotSubsystem#isDisabled()} and {@link DreadbotSubsystem#isEnabled()}
 * <p>
 * See {@link SubsystemBase}, {@link AutoCloseable}, {@link MotorSafeSystem}
 */
@SuppressWarnings("SpellCheckingInspection")
public abstract class DreadbotSubsystem extends SubsystemBase implements AutoCloseable, MotorSafeSystem {
    /**
     * Determines whether internal hardware of the subsystem is crashing or failing.
     * Once disabled, it is not possible to re-enable the subsystem.
     */
    private boolean enabled = true;

    protected DreadbotSubsystem() { }

    /**
     * Disable the subsystem for safety reasons. The subsystem will not be enabled
     * again until re-deploy. This is a final action.
     */
    public void disable() {
        if(enabled) {
            DriverStation.reportWarning(getName() + " is disabled!", false);
        }

        this.enabled = false;
    }

    /**
     * Determines whether the subsystem hardware is disabled.
     * @return disabled subsystem status
     */
    public boolean isDisabled() {
        return !enabled;
    }

    /**
     * Determines whether the subsystem hardware is enabled.
     * @return enabled subsystem status
     */
    public boolean isEnabled() {
        return enabled;
    }
}
