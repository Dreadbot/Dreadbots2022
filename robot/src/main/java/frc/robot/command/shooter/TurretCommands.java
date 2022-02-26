package frc.robot.command.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    public static class TurretTrackingCommand extends CommandBase {
        private final Turret turret;

        public TurretTrackingCommand(Turret turret) {
            this.turret = turret;

            addRequirements(turret);
        }

        /**
         * The main body of a command. Called repeatedly while the command is scheduled.
         */
        @Override
        public void execute() {
            if(!VisionInterface.canTrackHub()) return;

            double relativeAngleToHub = VisionInterface.getRelativeAngleToHub();
            double currentTurretAngle = turret.getAngle();

            double requestedAngle = currentTurretAngle + relativeAngleToHub;

            turret.setAngle(requestedAngle);
        }
    }
}
