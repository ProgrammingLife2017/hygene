package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the number of incoming edges of a node.
 */
public final class ColorSchemeIncomingEdges extends AbstractColorSchemeMinMax {
    private static final int DEFAULT_MAX = 5;


    /**
     * Creates instance of {@link ColorSchemeIncomingEdges} with max value set to {@value DEFAULT_MAX}.
     */
    public ColorSchemeIncomingEdges() {
        super(DEFAULT_MAX);
    }


    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getNumberOfIncomingEdges());
    }
}
