package frc.robot.command.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.shooter.Hood;

public class HoodCalibrationCommand extends SequentialCommandGroup {
    public HoodCalibrationCommand(Hood hood) {
        addRequirements(hood);
        addCommands(
            new HoodCalibrationUpCommand(hood),
            new HoodCalibrationDownCommand(hood)
        );
    }
}

class HoodCalibrationUpCommand extends CommandBase {
    private final Hood hood;

    public HoodCalibrationUpCommand(Hood hood) {
        this.hood = hood;

        addRequirements(hood);
    }

    @Override
    public void execute() {
        if(hood.getUpperLimitSwitch()) {
            hood.setSpeed(0.0d);
            return;
        }

        hood.setSpeed(0.6d);
        hood.setUpperMotorLimit(hood.getPosition());
    }

    @Override
    public boolean isFinished() {
        return hood.getUpperLimitSwitch();
    }
}

class HoodCalibrationDownCommand extends CommandBase {
    private final Hood hood;

    public HoodCalibrationDownCommand(Hood hood) {
        this.hood = hood;

        addRequirements(hood);
    }

    @Override
    public void execute() {
        if(hood.getLowerLimitSwitch()) {
            hood.setSpeed(0.0d);
            return;
        }

        hood.setSpeed(-0.6d);
        hood.setLowerMotorLimit(hood.getPosition());
    }

    @Override
    public boolean isFinished() {
        return hood.getLowerLimitSwitch();
    }
}