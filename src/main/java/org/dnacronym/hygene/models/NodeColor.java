package org.dnacronym.hygene.models;

import javafx.scene.paint.Color;


/**
 * Represents the color of node for the graph.
 */
public enum NodeColor {
    GREEN(Color.GREEN),
    ORANGE(Color.ORANGE),
    PURPLE(Color.PURPLE),
    YELLOW(Color.YELLOW),
    BLACK(Color.BLACK);

    private Color color;


    /**
     * Construct a {@link NodeColor} with given {@link Color}.
     *
     * @param color {@link Color} for use by JavaFX.
     */
    NodeColor(final Color color) {
        this.color = color;
    }


    /**
     * Converts a DNA sequence to the appropriate color.
     *
     * @param sequence DNA sequence
     * @return appropriate color for DNA sequence.
     */
    public static NodeColor sequenceToColor(final String sequence) {
        switch (sequence.charAt(0)) {
            case 'A':
                return GREEN;
            case 'C':
                return ORANGE;
            case 'G':
                return PURPLE;
            case 'T':
                return YELLOW;
            default:
                return BLACK;
        }
    }

    /**
     * Get the {@link Color} of this color for use by {@code JavaFX}.
     *
     * @return color for use by JavaFX
     */
    public Color getFXColor() {
        return color;
    }
}
