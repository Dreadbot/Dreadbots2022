// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.util;

/**
 * Provides several functions and utilities common to robot code development,
 * including range functions, joystick deadband calculators, and drive value
 * normalizers.
 */
public abstract class DreadbotMath {

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
     * @param inclusive   Whether or not the bounds inputValue and bottomValue are included as part of the range.
     * @return Returns 'true' if the inputValue is within the constraints, 'false' otherwise.
     */
    public static <T extends Comparable<T>> boolean inRange(final T inputValue, final T bottomValue,
                                                            final T topValue, final boolean inclusive) {
        if(!inclusive) 
            return inputValue.compareTo(bottomValue) > 0 && inputValue.compareTo(topValue) < 0;
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
    public static <T extends Comparable<T>> boolean inRange(final T inputValue, final T bottomValue,
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
    public static <T extends Comparable<T>> T clampValue(final T inputValue, final T clampBottom,
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
     * Determines whether the input is within a set of given constraints. If it is, this function
     * returns a 'zero' or otherwise neutral value. This function is useful for preventing noise
     * or unwanted input from controllers.
     *
     * @param <T>                 Comparable type.
     * @param inputValue          The given value to use against the deadband constraints.
     * @param deadbandZoneMinimum The bottom value of the given constraints.
     * @param deadbandZoneMaximum The maximum value of the given constraints.
     * @param neutralValue        The value to return when the input is within the given constraints.
     * @return The output of the deadband function.
     */
    public static <T extends Comparable<T>> T applyDeadbandToValue(final T inputValue, final T deadbandZoneMinimum,
                                                                   final T deadbandZoneMaximum, final T neutralValue) {
        if(DreadbotMath.inRange(inputValue, deadbandZoneMinimum, deadbandZoneMaximum, false))
            return neutralValue;

        return inputValue;
    }

    /**
     * Normalizes an array of given values to fit between -1.0 and 1.0.
     * <p>
     * Normalization is especially important for drive code, as most
     * SpeedControllers only accept values between -1.0 and 1.0, since these values
     * represent percentages of voltage.
     *
     * @param values The given values to normalize.
     */
    public static void normalizeValues(final double[] values) {
        final double[] absoluteValues = new double[values.length];
        for (int i = 0; i < absoluteValues.length; i++)
            absoluteValues[i] = Math.abs(values[i]);

        final double magnitude = DreadbotMath.maximumElement(absoluteValues);
        if (magnitude > 1.0)
            for (int i = 0; i < values.length; i++)
                values[i] /= magnitude;
    }

    /**
     * Normalizes an array of given values to fit between -1.0f and 1.0f.
     * <p>
     * Normalization is especially important for drive code, as most
     * SpeedControllers only accept values between -1.0 and 1.0, since these values
     * represent percentages of voltage.
     *
     * @param values The given values to normalize.
     * @return The values, now normalized.
     */
    public static void normalizeValues(final float[] values) {
        final float[] absoluteValues = new float[values.length];
        for (int i = 0; i < absoluteValues.length; i++)
            absoluteValues[i] = Math.abs(values[i]);

        final float magnitude = DreadbotMath.maximumElement(absoluteValues);
        if (magnitude > 1.0)
            for (int i = 0; i < values.length; i++)
                values[i] /= magnitude;
    }

    /**
     * Finds and returns the maximum element in an array of given elements.
     *
     * @param values The given values to search.
     * @return The greatest element in the array.
     */
    public static double maximumElement(final double[] values) {
        double currentMaximumElement = values[0];

        for (final double element : values)
            if (element > currentMaximumElement)
                currentMaximumElement = element;

        return currentMaximumElement;
    }

    /**
     * Finds and returns the maximum element in an array of given elements.
     *
     * @param values The given values to search.
     * @return The greatest element in the array.
     */
    public static float maximumElement(final float[] values) {
        float currentMaximumElement = values[0];

        for (final float element : values)
            if (element > currentMaximumElement)
                currentMaximumElement = element;

        return currentMaximumElement;
    }
}