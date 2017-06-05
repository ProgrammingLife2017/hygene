package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the sequence length.
 */
final class NodeColorSequenceLength extends AbstractNodeColorMinMax {
    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getSequenceLength());
    }
}
