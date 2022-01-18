package frc.robot.subsystem;

public abstract class Subsystem implements AutoCloseable {
    protected final String name;
    protected boolean enabled;

    public Subsystem(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public Subsystem(String name) {
        this.name = name;
        this.enabled = true;
    }

    protected abstract void stopMotors();

    public void enable() {
        if(enabled) return;

        this.enabled = true;
    }

    public void disable() {
        if(!enabled) return;

        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
