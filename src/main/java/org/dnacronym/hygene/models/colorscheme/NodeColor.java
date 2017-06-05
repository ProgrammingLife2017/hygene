package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * Classes that implement this interface decide the color of nodes based on the set mode.
 */
public interface NodeColor {
    /**
     * Calculate the color of a node based on the set values and the color mode.
     *
     * @param node the node which has to be colored
     * @return the color of the node
     */
    Color calculateColor(final Node node);
}
