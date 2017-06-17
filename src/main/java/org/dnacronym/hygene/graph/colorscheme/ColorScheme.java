package org.dnacronym.hygene.graph.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;


/**
 * Classes that implement this interface decide the color of nodes based on the set mode.
 */
@SuppressWarnings("squid:S1609") // This isn't a functional interface.
public interface ColorScheme {
    /**
     * Calculates the {@link Color} of a {@link NewNode} based on its attributes and the {@link Color} mode.
     *
     * @param node the node which has to be colored
     * @return the {@link Color} of the node
     */
    Color calculateColor(NewNode node);
}
