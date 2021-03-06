package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;


/**
 * Classes that extend from this class use a certain value of a node to calculate the color.
 * <p>
 * The color is determined as an interpolation of the minimum and maximum color, with the interpolation of the minimum
 * and maximum color being determined by said value of the {@link org.dnacronym.hygene.graph.node.Node} and its
 * position in the scale of {@code [0, max value]}.
 */
public abstract class ColorSchemeMinMax implements ColorScheme {
    private final int maxValue;
    private final Color minColor;
    private final Color maxColor;


    /**
     * Creates an instance of {@link ColorSchemeMinMax}.
     *
     * @param maxValue the max value before max {@link Color} is given
     * @param minColor the minimum color of the color scheme
     * @param maxColor the maximum color of the color scheme
     */
    public ColorSchemeMinMax(final int maxValue, final Color minColor, final Color maxColor) {
        this.maxValue = maxValue;

        this.minColor = minColor;
        this.maxColor = maxColor;
    }


    /**
     * Calculates the color based on the passed value, max value, min color and max color.
     * <p>
     * The color is determined to be an interpolation of the minimum and maximum color. The interpolation amount is on a
     * scale from 0 to 1, and determined to be the value of the value divided by the max value capped off at 1.
     *
     * @param value the value to decide the color for
     * @return the color of the value
     */
    final Color calculateColor(final double value) {
        return minColor.interpolate(maxColor, Math.min(1, value / maxValue));
    }
}
