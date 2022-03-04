package frc.robot.subsystem.shooter;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants;
import frc.robot.subsystem.DreadbotSubsystem;

public class ColorSensor extends DreadbotSubsystem {
    private ColorSensorV3 sensor;
    private ColorMatch colorMatch;

    private Color initialBallColor;

    private final double colorConfidence = 0.9;
    private double currentConfidence;

    public ColorSensor() {
        disable();
    }

    public ColorSensor(ColorSensorV3 sensor)
    {
        this.sensor = sensor;
        colorMatch = new ColorMatch();
        colorMatch.addColorMatch(Constants.COLOR_RED);
        colorMatch.addColorMatch(Constants.COLOR_BLUE);
        colorMatch.setConfidenceThreshold(colorConfidence);
    }

    @Override
    public void periodic() {
        String colorName;
        Color ballColor = getBallColor();

        if (ballColor == Constants.COLOR_RED) {
            colorName = "RED";
        } else if (ballColor == Constants.COLOR_BLUE) {
            colorName = "BLUE";
        } else {
            colorName = "NO MATCH";
        }

        SmartDashboard.putString("Color:", colorName);
    }

    public Color getBallColor()
    {
        if(isDisabled()) return null;

        ColorMatchResult matchColor = colorMatch.matchColor(sensor.getColor());
        if(matchColor == null) return null;

        currentConfidence = matchColor.confidence;
        if(matchColor.color == Constants.COLOR_RED) return Constants.COLOR_RED;
        if(matchColor.color == Constants.COLOR_BLUE) return Constants.COLOR_BLUE;
        return null;
    }

    public void prepareInitialShootConditions() {
        initialBallColor = getBallColor();
    }

    public boolean isBallDetected() {
        prepareInitialShootConditions();
        return initialBallColor != null;
    }

    @Override
    public void stopMotors() {
        if(isDisabled()) return;
        // nothing to do
    }

    @Override
    public void close() throws Exception {
        if(isDisabled()) return;
        // nothing to close
    }

    public Color getInitialBallColor() {
        return initialBallColor;
    }
}