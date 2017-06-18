package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;


/**
 * The color is determined by the sequence length.
 */
public final class ColorSchemeSequenceLength extends ColorSchemeMinMax {
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
    public Color calculateColor(final LayoutableNode node) {
        return calculateColor(node.getLength());
    }
}
