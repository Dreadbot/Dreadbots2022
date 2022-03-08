// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.util;

/**
 * Provides several functions and utilities common to robot code development,
 * including range functions, joystick deadband calculators, and drive value
 * normalizers.
 */
@SuppressWarnings("SpellCheckingInspection")
public interface DreadbotMath {

    /**
     * In-Range Function
     * <p>
     * The in-range function determines whether a value is 'in-between' a given minimum
     * value and a maximum value.
     *
     * @param <T>         Comparable type.
     * @param inputValue  The input value.
     * @param bottomValue The bottom value of the range.
     * @param topValue    The top value of the range.
     * @param inclusive   Whether the bounds inputValue and bottomValue are included as part of the range.
     * @return Returns 'true' if the inputValue is within the constraints, 'false' otherwise.
     */
    static <T extends Comparable<T>> boolean inRange(final T inputValue, final T bottomValue,
                                                     final T topValue, final boolean inclusive) {
        if(!inclusive) {
            if(topValue == bottomValue) return false;
            return inputValue.compareTo(bottomValue) > 0 && inputValue.compareTo(topValue) < 0;
        }
        
        if(topValue == bottomValue) return topValue == inputValue;
        return inputValue.compareTo(bottomValue) >= 0 && inputValue.compareTo(topValue) <= 0;
    }

    /**
     * In-Range Function
     * <p>
     * The in-range function determines whether a value is 'in-between' a given minimum
     * value and a maximum value.
     *
     * @param <T>         Comparable type.
     * @param inputValue  The input value.
     * @param bottomValue The bottom value of the range.
     * @param topValue    The top value of the range.
     * @return Returns 'true' if the inputValue is within the constraints, 'false' otherwise.
     */
    static <T extends Comparable<T>> boolean inRange(final T inputValue, final T bottomValue,
                                                     final T topValue) {
        return DreadbotMath.inRange(inputValue, bottomValue, topValue, true);
    }

    /**
     * Clamp Function
     * <p>
     * Clamp functions will 'clamp' a value between two constraints, such that
     * the returned value will be no greater than the 'top' of the clamp and
     * no less than the 'bottom' of the clamp.
     *
     * @param <T>         Comparable type.
     * @param inputValue  The given value to clamp.
     * @param clampBottom The bottom of the clamp constraints.
     * @param clampTop    The top of the clamp constraints.
     * @return The output of the clamp function.
     */
    static <T extends Comparable<T>> T clampValue(final T inputValue, final T clampBottom,
                                                  final T clampTop) {
        if (inputValue.compareTo(clampBottom) < 0)
            return clampBottom;
        if (inputValue.compareTo(clampTop) > 0)
            return clampTop;

        return inputValue;
    }

    /**
     * Deadband Processing function
     * <p>
     * Determines whether the input (double) is within the deadband range. If it is, this
     * function returns 0.0d. This function is useful for preventing noise or unwanted input
     * from controllers.
     *
     * @param inputValue The given value to test against the deadband constraints.
     * @param deadband   The deadband range.
     * @return The output of the deadband function.
     */
    static double applyDeadbandToValue(final double inputValue, final double deadband) {
        return applyDeadbandToValue(inputValue, -deadband, deadband, 0.0d);
    }

    /**
     * Deadband Processing function
     * <p>
     * Determines whether the Comparable input is within a set of given constraints. If it is, this
     * function returns a 'zero' or otherwise neutral value. This function is useful for preventing
     * noise or unwanted input from controllers.
     *
     * @param <T>                 Comparable type.
     * @param inputValue          The given value to test against the deadband constraints.
     * @param deadbandZoneMinimum The bottom value of the given constraints.
     * @param deadbandZoneMaximum The maximum value of the given constraints.
     * @param neutralValue        The value to return when the input is within the given constraints.
     * @return The output of the deadband function.
     */
    static <T extends Comparable<T>> T applyDeadbandToValue(final T inputValue, final T deadbandZoneMinimum,
                                                            final T deadbandZoneMaximum, final T neutralValue) {
        if(DreadbotMath.inRange(inputValue, deadbandZoneMinimum, deadbandZoneMaximum, false))
            return neutralValue;

        return inputValue;
    }
}