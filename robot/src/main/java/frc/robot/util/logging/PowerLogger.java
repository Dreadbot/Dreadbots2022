package frc.robot.util.logging;

import edu.wpi.first.wpilibj.PowerDistribution;
import frc.robot.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class PowerLogger {
    private FileWriter fileWriter;
    private PowerDistribution powerDistro;
    private boolean disabled = !Constants.VOLTAGE_REPORTING;

    public PowerLogger() {
        if (disabled) {
            return;
        }

        powerDistro = new PowerDistribution(22, PowerDistribution.ModuleType.kRev);
        try {
            fileWriter = new FileWriter("/C/PowerLog:" + new Date() + new Date().getTime() + ".txt");
            fileWriter.write("--PDP Power log--\n");
            fileWriter.write("Port Number:,");
            for (int i = 0; i < 24; i++) {
                fileWriter.write(i + ",");
            }

            fileWriter.write("\n");
        } catch (
                IOException e) {
            e.printStackTrace();
            System.err.println("BROKEN");
        }
    }

    public void logPower() {
        if (disabled) {
            return;
        }

        if (powerDistro.getTotalCurrent() >= 40.0d && Constants.VOLTAGE_REPORTING) {
            reportCurrents();
        }
    }

    private void reportCurrents() {
        if (disabled) {
            return;
        }

        String powerOutput = "Power output:,";
        for (int i = 0; i < 24; i++) {
            powerOutput += powerDistro.getCurrent(i) + ",";
        }

        try {
            fileWriter.write(new Timestamp(new Date().getTime()) + " " + powerOutput + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (disabled) {
            return;
        }

        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
