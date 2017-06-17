package org.dnacronym.hygene.graph.colorscheme.fixed;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;


/**
 * The {@link Color} is fixed, and not dependent on any specific {@link NewNode}.
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
    public Color calculateColor(final NewNode node) {
        return color;
    }
}
