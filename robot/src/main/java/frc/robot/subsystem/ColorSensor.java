package frc.robot.subsystem;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ColorSensor {
    private ColorSensorV3 sensor;
    private ColorMatch colorMatch;

    public ColorSensor(ColorSensorV3 sensor)
    {
        this.sensor = sensor;
    }
    
    public void printColor() // for testing and finding color, switch to rio log if possible
    {
        SmartDashboard.putString("Color:", (sensor.getRed() + "," + sensor.getGreen() + "," + sensor.getBlue()));
    }
}
