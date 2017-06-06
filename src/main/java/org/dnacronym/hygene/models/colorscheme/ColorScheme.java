package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * Classes that implement this interface decide the color of nodes based on the set mode.
 */
public interface ColorScheme {
    /**
     * Calculates the {@link Color} of a {@link Node} based on its attributes and the {@link Color} mode.
     *
     * @param node the node which has to be colored
     * @return the {@link Color} of the node
     */
    Color calculateColor(final Node node);
}
