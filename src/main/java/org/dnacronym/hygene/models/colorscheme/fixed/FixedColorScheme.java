package org.dnacronym.hygene.models.colorscheme.fixed;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.colorscheme.ColorScheme;


/**
 * The {@link Color} is fixed, and not dependent on any specific {@link Node}.
 */
public final class FixedColorScheme implements ColorScheme {
    private Color color;


    /**
     * Creates instance of a {@link FixedColorScheme} with the {@link Color} set to {@link Color#PURPLE}.
     */
    public FixedColorScheme() {
        this.color = Color.PURPLE;
    }


    /**
     * Sets the fixed {@link Color}.
     *
     * @param color the fixed {@link Color}
     */
    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public Color calculateColor(final Node node) {
        return color;
    }
}
