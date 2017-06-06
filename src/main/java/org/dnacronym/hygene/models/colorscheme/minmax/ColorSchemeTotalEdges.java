package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the number of incoming and outgoing edges of a node combined.
 */
public final class ColorSchemeTotalEdges extends AbstractColorSchemeMinMax {
    private static final int DEFAULT_MAX = 10;


    /**
     * Creates an instance of {@link ColorSchemeTotalEdges} with max value set to {@value DEFAULT_MAX}.
     */
    public ColorSchemeTotalEdges() {
        super(DEFAULT_MAX);
    }


    @Override
    @SuppressWarnings("squid:S2184") // The amounts are discrete
    public Color calculateColor(final Node node) {
        return calculateColor(node.getNumberOfIncomingEdges() + node.getNumberOfOutgoingEdges());
    }
}
