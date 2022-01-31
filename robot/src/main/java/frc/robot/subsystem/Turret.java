package frc.robot.subsystem;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Turret extends Subsystem {
    private DigitalInput leftSwitch;
    private DigitalInput rightSwitch;

    public Turret(DigitalInput leftSwitch, DigitalInput rightSwitch) {
        super("Turret");
        this.leftSwitch = leftSwitch;
        this.rightSwitch = rightSwitch;
        SmartDashboard.putBoolean("left", getLeftLimitSwitch());
        SmartDashboard.putBoolean("right", getRightLimitSwitch());
    }

    @Override
    public void close() throws Exception {
        leftSwitch.close();
        rightSwitch.close();
    }
    public boolean getLeftLimitSwitch() {
        return !leftSwitch.get();
    }

    public boolean getRightLimitSwitch() {
        return !rightSwitch.get();
    }
    public void switchDebug() {
        SmartDashboard.putBoolean("left", getLeftLimitSwitch());
        SmartDashboard.putBoolean("right", getRightLimitSwitch());
    }
    @Override
    protected void stopMotors() {
        // TODO Auto-generated method stub
        
    }
    
}
