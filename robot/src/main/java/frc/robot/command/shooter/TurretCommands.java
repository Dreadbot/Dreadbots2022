package frc.robot.command.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    public static class TurretTrackingCommand extends CommandBase {
        private final Turret turret;
        private double kPTurret;

        private double lastRelativeAngleToHub;

        public TurretTrackingCommand(Turret turret) {
            this.turret = turret;
            kPTurret = SmartDashboard.getNumber("TURRET P", 0.1);
            SmartDashboard.putNumber("TURRET P", kPTurret);

            addRequirements(turret);
        }

        /**
         * The main body of a command. Called repeatedly while the command is scheduled.
         */
        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            // Fetch current vision relative angle
            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub();
            if(relativeAngleToHub == lastRelativeAngleToHub) return;
            SmartDashboard.putNumber("DEBUG RA", relativeAngleToHub);

            kPTurret = SmartDashboard.getNumber("TURRET P", 0.1);
            // Calculate the commanded absolute angle from relative
            double currentTurretAngle = turret.getAngle();
            double finalAngle = currentTurretAngle + (kPTurret * relativeAngleToHub);

            SmartDashboard.putNumber("DEBUG REQA", finalAngle);

            // Command hardware and update state
            turret.setAngle(finalAngle);
            lastRelativeAngleToHub = relativeAngleToHub;
        }
    }

    private TurretCommands() { }
}
