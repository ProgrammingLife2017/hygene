package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the number of incoming edges of a node.
 */
final class NodeColorIncomingEdges extends AbstractNodeColorMinMax {
    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getNumberOfIncomingEdges());
    }
}
