package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.LayoutableNode;


/**
 * The color is determined by the amount of outgoing edges of a node.
 */
public final class ColorSchemeOutgoingEdges extends ColorSchemeMinMax {
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
    public Color calculateColor(final LayoutableNode node) {
        return calculateColor(node.getOutgoingEdges().size());
    }
}
