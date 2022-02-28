package frc.robot.command.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.shooter.Turret;
import frc.robot.util.VisionInterface;

public class TurretCommands {
    public static class TurretTrackingCommand extends CommandBase {
        private final PIDController visionErrorPIDController;
        private final Turret turret;

        private double lastRelativeAngleToHub;

        public TurretTrackingCommand(Turret turret) {
            this.turret = turret;
            this.visionErrorPIDController = new PIDController(1.0d, 0.0d, 0.0d);
            SmartDashboard.putData("VISION TURRET PID", visionErrorPIDController);

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

            // Calculate the commanded absolute angle from relative
            double currentTurretAngle = turret.getAngle();
            double finalAngle = currentTurretAngle - relativeAngleToHub;

            visionErrorPIDController.setSetpoint(finalAngle);
            double requestedAngle = visionErrorPIDController.calculate(currentTurretAngle);

            SmartDashboard.putNumber("DEBUG REQA", requestedAngle);

            // Command hardware and update state
            turret.setAngle(requestedAngle);
            lastRelativeAngleToHub = relativeAngleToHub;
        }
    }

    private TurretCommands() { }
}
