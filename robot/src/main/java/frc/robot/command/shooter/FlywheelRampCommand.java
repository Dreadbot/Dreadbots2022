package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Shooter;

public class FlywheelRampCommand extends CommandBase {
    private final Shooter shooter;

    public FlywheelRampCommand(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void execute() {
        // TODO remove SmartDashboard settings, use vision/distance input
        double setPoint = SmartDashboard.getNumber("RPM", 0);

        shooter.rampFlywheelToSpeed(setPoint);
        
        SmartDashboard.putNumber("Flywheel Velocity (RPM)", shooter.getFlywheelVelocity());
    }
    
}
