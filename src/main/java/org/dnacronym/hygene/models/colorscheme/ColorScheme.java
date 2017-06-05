package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * Scheme for deciding color of node.
 */
public final class ColorScheme {
    private ColorMode colorModus;

    public void setColorModus(final COLOR_MODE colorMode) {
        switch (colorMode) {
            case SEQUENCE_LENGTH:
                this.colorModus = new ColorModeSequenceLength();
                break;
            default:
                throw new IllegalArgumentException(colorMode + " was not recognized.");
        }
    }

    public Color getColor(final Node node) {
        return colorModus.calculateColor(node);
    }

    public enum COLOR_MODE {
        SEQUENCE_LENGTH
    }
}
