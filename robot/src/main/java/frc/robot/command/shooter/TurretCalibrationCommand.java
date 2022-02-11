package frc.robot.command.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.Turret;

public class TurretCalibrationCommand extends SequentialCommandGroup {
    public TurretCalibrationCommand(Turret turret) {
        addRequirements(turret);
        addCommands(
            new TurretCalibrationRightCommand(turret),
            new TurretCalibrationLeftCommand(turret)
        );
    }
}

class TurretCalibrationRightCommand extends CommandBase {
    private final Turret turret;

    public TurretCalibrationRightCommand(Turret turret) {
        this.turret = turret;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        if(turret.getRightLimitSwitch()) {
            turret.setSpeed(0.0d);
            return;
        }

        turret.setSpeed(-0.3);
        turret.setRightMotorLimit(turret.getPosition());
        SmartDashboard.putNumber("Turret Upper", turret.getPosition());
    }

    @Override
    public boolean isFinished() {
        return turret.getRightLimitSwitch();
    }
}

class TurretCalibrationLeftCommand extends CommandBase {
    private final Turret turret;

    public TurretCalibrationLeftCommand(Turret turret) {
        this.turret = turret;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        if(turret.getLeftLimitSwitch()) {
            turret.setSpeed(0.0d);
            return;
        }

        turret.setSpeed(0.3);
        turret.setLeftMotorLimit(turret.getPosition());
        SmartDashboard.putNumber("Turret Lower", turret.getPosition());
    }

    @Override
    public boolean isFinished() {
        return turret.getLeftLimitSwitch();
    }
}