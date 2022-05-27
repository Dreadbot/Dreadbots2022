// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.util.controls;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * A custom Facade pattern for the default WPI Joystick class
 * designed for simplicity of use.
 * This facade has only been tested on the Logitech F310 Gamepad.
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public final class DreadbotController {
    private final Joystick joystick;

    /**
     * Constructs a DreadbotController object with the ID of the gamepad HID, which is
     * passed to the WPI Joystick class underneath.
     *
     * @param joystickPort The port on the Driver Station that the joystick is plugged into.
     */
    public DreadbotController(int joystickPort) {
        this.joystick = new Joystick(joystickPort);
    }

    /**
     * @return The value of the side-to-side motion on the left joystick.
     */
    public double getXAxis() {
        return joystick.getRawAxis(0);
    }

    /**
     * @return The value of the forward/backward motion on the left joystick.
     */
    public double getYAxis() {
        return joystick.getRawAxis(1);
    }

    /**
     * @return The value of the side-to-side motion on the right joystick.
     */
    public double getZAxis() {
        return joystick.getRawAxis(2);
    }

    /**
     * @return The value of the forward/backward motion on the right joystick.
     */
    public double getWAxis() {
        return joystick.getRawAxis(3);
    }

    /**
     * @return The state of the blue 'X' button on the right side of the gamepad.
     */
    public boolean isXButtonPressed() {
        return joystick.getRawButton(1);
    }

    /**
     * @return The joystick button reference to the blue 'X button' on the right hand side of the gamepad.
     */
    public JoystickButton getXButton() {
        return new JoystickButton(joystick, 1);
    }

    /**
     * @return The state of the green 'A' button on the right side of the gamepad.
     */
    public boolean isAButtonPressed() {
        return joystick.getRawButton(2);
    }

    /**
     * @return The joystick button reference to the green 'A' button on the right hand side of the gamepad.
     */
    public JoystickButton getAButton() {
        return new JoystickButton(joystick, 2);
    }

    /**
     * @return The state of the red 'B' button on the right side of the gamepad.
     */
    public boolean isBButtonPressed() {
        return joystick.getRawButton(3);
    }

    /**
     * @return The joystick button reference to the red 'B' button on the right hand side of the gamepad.
     */
    public JoystickButton getBButton() {
        return new JoystickButton(joystick, 3);
    }

    /**
     * @return The state of the orange 'Y' button on the right side of the gamepad.
     */
    public boolean isYButtonPressed() {
        return joystick.getRawButton(4);
    }

    /**
     * @return The joystick button reference to the orange 'Y' button on the right hand side of the gamepad.
     */
    public JoystickButton getYButton() {
        return new JoystickButton(joystick, 4);
    }

    /**
     * @return The state of the left flat button on the front face of the gamepad.
     */
    public boolean isLeftBumperPressed() {
        return joystick.getRawButton(5);
    }

    /**
     * @return The joystick button reference to the left flat button on the front face of the gamepad.
     */
    public JoystickButton getLeftBumper() {
        return new JoystickButton(joystick, 5);
    }

    /**
     * @return The state of the right flat button on the front face of the gamepad.
     */
    public boolean isRightBumperPressed() {
        return joystick.getRawButton(6);
    }

    /**
     * @return The joystick button reference to the right flat button on the front face of the gamepad.
     */
    public JoystickButton getRightBumper() {
        return new JoystickButton(joystick, 6);
    }

    /**
     * @return The state of the left trigger on the front face of the gamepad.
     */
    public boolean isLeftTriggerPressed() {
        return joystick.getRawButton(7);
    }

    /**
     * @return The joystick button reference to the left trigger on the front face of the gamepad.
     */
    public JoystickButton getLeftTrigger() {
        return new JoystickButton(joystick, 7);
    }

    /**
     * @return The state of the right trigger on the front face of the gamepad.
     */
    public boolean isRightTriggerPressed() {
        return joystick.getRawButton(8);
    }

    /**
     * @return The joystick button reference to the right trigger on the front face of the gamepad.
     */
    public JoystickButton getRightTrigger() {
        return new JoystickButton(joystick, 8);
    }

    /**
     * @return The state of the 'BACK' utility button in the center of the gamepad.
     */
    public boolean isBackButtonPressed() {
        return joystick.getRawButton(9);
    }

    /**
     * @return The joystick button reference to the 'BACK' utility button in the center of the gamepad.
     */
    public JoystickButton getBackButton() {
        return new JoystickButton(joystick, 9);
    }

    /**
     * @return The state of the 'START' utility button in the center of the gamepad.
     */
    public boolean isStartButtonPressed() {
        return joystick.getRawButton(10);
    }

    /**
     * @return The joystick button reference to the 'START' utility button in the center of the gamepad.
     */
    public JoystickButton getStartButton() {
        return new JoystickButton(joystick, 10);
    }

    /**
     * @return The state of the Dpad being pressed up
     */
    public boolean isDpadUpPressed() {
        return joystick.getPOV() == 0;
    }

    /**
     * @return The reference of the Dpad being pressed up
     */
    public JoystickDpad getDpadUp(){
        return new JoystickDpad(joystick, 0);
    }

    /**
     * @return The state of the Dpad being pressed right
     */
    public boolean isDpadRightPressed() {
        return joystick.getPOV() == 90;
    }

    /**
     * @return The reference of the Dpad being pressed right
     */
    public JoystickDpad getDpadRight(){
        return new JoystickDpad(joystick, 90);
    }

    /**
     * @return The state of the Dpad being pressed down
     */
    public boolean isDpadDownPressed() {
        return joystick.getPOV() == 180;
    }

    /**
     * @return The reference of the Dpad being pressed right
     */
    public JoystickDpad getDpadDown(){
        return new JoystickDpad(joystick, 180);
    }

    /**
     * @return The state of the Dpad being pressed left
     */
    public boolean isDpadLeftPressed() {
        return joystick.getPOV() == 270;
    }

    /**
     * @return The reference of the Dpad being pressed right
     */
    public JoystickDpad getDpadLeft(){
        return new JoystickDpad(joystick, 270);
    }

    /**
     * Returns the lower level edu.wpi.first.wpilibj.Joystick class used by this
     * facade pattern.
     *
     * @return The native WPI object used to construct this Facade pattern with.
     */
    public Joystick getNativeWPIJoystick() {
        return joystick;
    }

    /**
     * Delegate method to the getRawButton(int) method of the Joystick class.
     *
     * @param button The ID of the button on the gamepad.
     * @return The state of the given button.
     */
    public boolean getRawButton(int button) {
        return joystick.getRawButton(button);
    }

    /**
     * Delegate method to the getRawAxis(int) method of the Joystick class.
     *
     * @param axis The ID of the axis on the gamepad.
     * @return The current position of the given axis.
     */
    public double getRawAxis(int axis) {
        return joystick.getRawAxis(axis);
    }

    /**
     * Delegate method to the isConnected(int) method of the Joystick class.
     *
     * @return Whether the gamepad is connected or not.
     */
    public boolean isConnected() {
        return joystick.isConnected();
    }

    /**
     * Delegate method to the getPOV() method of the Joystick class
     * 
     * @return The POV of the Dpad
     */
    public int getPOV() {
        return joystick.getPOV();
    }

    /**
     * Delegate method to the getName() method of the Joystick class.
     *
     * @return Name of the gamepad HID.
     */
    public String getName() {
        return joystick.getName();
    }
}