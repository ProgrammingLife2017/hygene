package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the sequence length.
 */
public final class ColorSchemeSequenceLength extends AbstractColorSchemeMinMax {
    /**
     * Creates an instance of {@link ColorSchemeSequenceLength}.
     *
     * @param maxValue the max value before max {@link Color} is given
     * @param minColor the minimum color of the color scheme
     * @param maxColor the maximum color of the color scheme
     */
    public ColorSchemeSequenceLength(final int maxValue, final Color minColor, final Color maxColor) {
        super(maxValue, minColor, maxColor);
    }


    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getSequenceLength());
    }
}
