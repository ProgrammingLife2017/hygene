package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.Node;


/**
 * The color is determined by the number of incoming edges of a node.
 */
public final class ColorSchemeIncomingEdges extends AbstractColorSchemeMinMax {
    /**
     * Creates an instance of {@link ColorSchemeIncomingEdges}.
     *
     * @param maxValue the max value before max {@link Color} is given
     * @param minColor the minimum color of the color scheme
     * @param maxColor the maximum color of the color scheme
     */
    public ColorSchemeIncomingEdges(final int maxValue, final Color minColor, final Color maxColor) {
        super(maxValue, minColor, maxColor);
    }


    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getIncomingEdges().size());
    }
}
