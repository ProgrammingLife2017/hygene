package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;


/**
 * Classes that extend from this class use a certain value of a node to calculate the color.
 * <p>
 * The color is determined as an interpolation of the minimum and maximum color, with the interpolation of the minimum
 * and maximum color being determined by said value of the {@link org.dnacronym.hygene.models.Node} and it's position
 * in the scale of {@code [0, max value]}.
 */
abstract class AbstractNodeColorMinMax implements NodeColor {
    private static final int DEFAULT_MAX_VALUE = 100;

    private int maxValue = DEFAULT_MAX_VALUE;
    private Color minColor = Color.BLUE;
    private Color maxColor = Color.RED;


    /**
     * Calculate the color based on the passes value, max value, min color and max color.
     * <p>
     * The color is determined to be an interpolation of the minimum and maximum color. The interpolation amount is on a
     * scale from 0 to 1, and determined to be the value of the value divided by the max value capped off at 1.
     *
     * @param value the value to decide the color for
     * @return the color of the value
     */
    protected Color calculateColor(final double value) {
        return minColor.interpolate(maxColor, Math.min(1, value / maxValue));
    }

    /**
     * Sets the max value.
     * <p>
     * If said value of node is equal to or greater than this value the node color is max color.
     *
     * @param maxValue the value at which max color is achieved
     */
    public void setMaxValue(final int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Sets the minimum color of the node.
     *
     * @param minColor the minimum color of the node
     */
    public void setMinColor(final Color minColor) {
        this.minColor = minColor;
    }

    /**
     * Sets the maximum color of the node.
     *
     * @param maxColor the maximum color of the node
     */
    public void setMaxColor(final Color maxColor) {
        this.maxColor = maxColor;
    }
}
