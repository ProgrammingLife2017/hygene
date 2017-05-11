package org.dnacronym.hygene.models;


/**
 * Represents the color of node for the graph.
 */
public enum NodeColor {
    GREEN,
    ORANGE,
    PURPLE,
    YELLOW,
    BLACK;


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
}
