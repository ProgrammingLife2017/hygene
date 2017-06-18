package org.dnacronym.hygene.graph.colorscheme.fixed;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;


/**
 * The {@link Color} is fixed, and not dependent on any specific {@link LayoutableNode}.
 */
public final class FixedColorScheme implements ColorScheme {
    private final Color color;


    /**
     * Creates an instance of a {@link FixedColorScheme} with the {@link Color} set to {@link Color#PURPLE}.
     *
     * @param color the fixed {@link Color}
     */
    public FixedColorScheme(final Color color) {
        this.color = color;
    }


    @Override
    public Color calculateColor(final LayoutableNode node) {
        return color;
    }
}
