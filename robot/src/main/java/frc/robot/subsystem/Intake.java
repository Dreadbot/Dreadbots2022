// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
    private final CANSparkMax motor;

    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

    public boolean isballblue() {
        Color detectedColor = m_colorSensor.getColor();
        double IR = m_colorSensor.getIR();
        double red1 = detectedColor.red;
        double green1 = detectedColor.green;
        double blue1 = detectedColor.blue;
        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);
        SmartDashboard.putNumber("IR", IR);
        if (red1>blue1){
            return false;
        }
        else {
            return true;
        }
        
    }

    public Intake(CANSparkMax motor) {
        this.motor = motor;
        
        if(!Constants.INTAKE_ENABLED) {
            motor.close();

            return;
        }

        motor.setInverted(true);
    }

    public void intake() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.set(1.0d);
    }

    public void outtake() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.set(-1.0d);
    }

    public boolean isIntaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        return motor.get() > 0.0d;
    }

    public boolean isOuttaking() {
        if(!Constants.INTAKE_ENABLED) return false;

        return motor.get() < 0.0d;
    }

    public void idle() {
        if(!Constants.INTAKE_ENABLED) return;
        
        motor.set(0.0d);
    }

    protected void stopMotors() {
        if(!Constants.INTAKE_ENABLED) return;

        motor.stopMotor();
    }

    public void close() throws Exception {
        if(!Constants.INTAKE_ENABLED) return;

        motor.close();
    }

    public CANSparkMax getMotor() {
        return motor;
    }
}
