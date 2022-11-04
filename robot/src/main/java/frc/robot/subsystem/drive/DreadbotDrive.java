package frc.robot.subsystem.drive;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;

public interface DreadbotDrive  {
    void periodic();

    void driveCartesian(double joystickForwardAxis, double joystickLateralAxis, double zRotation);

    ChassisSpeeds getChassisSpeeds();

    void setChassisSpeeds(ChassisSpeeds chassisSpeeds);

    MecanumDriveWheelSpeeds getWheelSpeeds();

    void setWheelVoltages(double leftFrontVoltage, double rightFrontVoltage,
                          double leftBackVoltage, double rightBackVoltage);

    void setWheelSpeeds(MecanumDriveWheelSpeeds wheelSpeeds);

    double getFrontEncoderAvg();

    void stopMotors();

    void close();

    double getYaw();

    AHRS getGyroscope();
}
