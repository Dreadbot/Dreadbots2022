package frc.robot.util;

/**
 * A class that applies a given percentage sensitivity to an input for output.
 * Controls how sensitive the output is given the input.
 */
public class SensitivityController {
    private double positivePercentageSensitivity;
    private double negativePercentageSensitivity;

    private double positiveSensitivityExponent;
    private double negativeSensitivityExponent;

    /**
     * Default constructor with -40% sensitivity across the system.
     */
    public SensitivityController() {
        this.positivePercentageSensitivity = -40.0d;
        this.negativePercentageSensitivity = -40.0d;

        recalculateSensitivityExponents();
    }

    /**
     * Constructor with sensitivity arguments.
     *
     * @param positivePercentageSensitivity The positive input region's output sensitivity
     * @param negativePercentageSensitivity The negative input region's output sensitivity
     */
    public SensitivityController(double positivePercentageSensitivity, double negativePercentageSensitivity) {
        this.positivePercentageSensitivity = DreadbotMath.clampValue(positivePercentageSensitivity, -100.0d, 100.0d);
        this.negativePercentageSensitivity = DreadbotMath.clampValue(negativePercentageSensitivity, -100.0d, 100.0d);

        recalculateSensitivityExponents();
    }

    /**
     * Filters the input to apply a sensitivity to the output.
     *
     * @param input The joystick input
     * @return The filtered output
     */
    public double calculate(double input) {
        if(input >= 0.0d) return Math.pow(input, positiveSensitivityExponent);
        return -Math.pow(-input, negativeSensitivityExponent);
    }

    private void recalculateSensitivityExponents() {
        positiveSensitivityExponent = positivePercentageSensitivity / 100.0d;
        positiveSensitivityExponent = Math.pow(10.0d, positiveSensitivityExponent);
        positiveSensitivityExponent = 1.0d / positiveSensitivityExponent;

        negativeSensitivityExponent = negativePercentageSensitivity / 100.0d;
        negativeSensitivityExponent = Math.pow(10.0d, negativeSensitivityExponent);
        negativeSensitivityExponent = 1.0d / negativeSensitivityExponent;
    }

    public void setPercentageSensitivity(double positivePercentageSensitivity, double negativePercentageSensitivity) {
        this.positivePercentageSensitivity = DreadbotMath.clampValue(positivePercentageSensitivity, -100.0d, 100.0d);
        this.negativePercentageSensitivity = DreadbotMath.clampValue(negativePercentageSensitivity, -100.0d, 100.0d);

        recalculateSensitivityExponents();
    }

    public double getPositivePercentageSensitivity() {
        return positivePercentageSensitivity;
    }

    public double getNegativePercentageSensitivity() {
        return negativePercentageSensitivity;
    }
}
