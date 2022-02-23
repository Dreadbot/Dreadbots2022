package frc.robot.subsystem.shooter;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants;

public class ColorSensor {
    private ColorSensorV3 sensor;
    private ColorMatch colorMatch;
    private double colorConfidence = 0.9;

    public ColorSensor(ColorSensorV3 sensor)
    {
        this.sensor = sensor;
        colorMatch = new ColorMatch();
        colorMatch.addColorMatch(Constants.COLOR_RED);
        colorMatch.addColorMatch(Constants.COLOR_BLUE);
        colorMatch.setConfidenceThreshold(colorConfidence);
    }

    public Color getBallColor()
    {
        ColorMatchResult matchColor = colorMatch.matchColor(sensor.getColor());

        if(matchColor == null)
        {
            SmartDashboard.putString("Color:", "Null");
            return null;
        }
        else if(matchColor.color == Constants.COLOR_RED)
        {
            SmartDashboard.putNumber("Confidence", matchColor.confidence);
            SmartDashboard.putString("Color:", "Red");
            return Constants.COLOR_RED;
        }
        else if(matchColor.color == Constants.COLOR_BLUE)
        {
            SmartDashboard.putNumber("Confidence", matchColor.confidence);
            SmartDashboard.putString("Color:", "Blue");
            return Constants.COLOR_BLUE;
        }
        return null; // Keeps compiler happy
    }
    
    public void printColor() // for testing and finding color, switch to rio log if possible
    {
        SmartDashboard.putNumber("Color R:", sensor.getColor().red );
        SmartDashboard.putNumber("Color G:", sensor.getColor().green);
        SmartDashboard.putNumber("Color B:", sensor.getColor().blue);
    }
}