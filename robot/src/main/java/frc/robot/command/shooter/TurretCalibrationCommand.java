package frc.robot.command.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.Turret;

public class TurretCalibrationCommand extends SequentialCommandGroup {
    public TurretCalibrationCommand(Turret turret) {
        addRequirements(turret);
        addCommands(
            new TurretUpperCalibrationCommand(turret),
            new TurretLowerCalibrationCommand(turret)
        );
    }
}

class TurretUpperCalibrationCommand extends CommandBase {
    private final Turret turret;

    public TurretUpperCalibrationCommand(Turret turret) {
        this.turret = turret;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        if(turret.getUpperLimitSwitch()) {
            turret.stopMotors();
            return;
        }

        turret.setSpeed(-0.1);
        turret.setUpperMotorLimit(turret.getPosition());
    }

    @Override
    public boolean isFinished() {
        return turret.getUpperLimitSwitch();
    }
}

class TurretLowerCalibrationCommand extends CommandBase {
    private final Turret turret;

    public TurretLowerCalibrationCommand(Turret turret) {
        this.turret = turret;

        addRequirements(turret);
    }

    @Override
    public void execute() {
        if(turret.getLowerLimitSwitch()) {
            turret.stopMotors();
            return;
        }

        turret.setSpeed(0.1);
        turret.setLowerMotorLimit(turret.getPosition());
    }

    @Override
    public boolean isFinished() {
        return turret.getLowerLimitSwitch();
    }
}