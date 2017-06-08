package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;


/**
 * The color is determined by the amount of outgoing edges of a node.
 */
public final class ColorSchemeOutgoingEdges extends AbstractColorSchemeMinMax {
    /**
     * Creates an instance of {@link ColorSchemeOutgoingEdges}.
     *
     * @param maxValue the max value before max {@link Color} is given
     * @param minColor the minimum color of the color scheme
     * @param maxColor the maximum color of the color scheme
     */
    public ColorSchemeOutgoingEdges(final int maxValue, final Color minColor, final Color maxColor) {
        super(maxValue, minColor, maxColor);
    }


    @Override
    public Color calculateColor(final NewNode node) {
        return calculateColor(node.getOutgoingEdges().size());
    }
}
