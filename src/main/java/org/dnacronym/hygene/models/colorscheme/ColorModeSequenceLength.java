package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * Color dependent on sequence length.
 */
final class ColorModeSequenceLength implements ColorMode {
    private int maxLength;
    private Color minColor;
    private Color maxColor;

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMinColor(final Color minColor) {
        this.minColor = minColor;
    }

    public void setMaxColor(final Color maxColor) {
        this.maxColor = maxColor;
    }

    @Override
    public Color calculateColor(final Node node) {
        return minColor.interpolate(maxColor, Math.min(1, (double) node.getSequenceLength() / maxLength));
    }
}
