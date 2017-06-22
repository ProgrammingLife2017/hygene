package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;


/**
 * A highlight type denotes what the highlight type is.
 */
public enum HighlightType {
    SELECTED(Color.rgb(0, 255, 46)),
    BOOKMARKED(Color.RED),
    HIGHLIGHTED(Color.BLACK),
    QUERIED(Color.PURPLE);

    private Color color;


    /**
     * Creates a new {@link HighlightType}.
     *
     * @param color the {@link Color} of the highlight type
     */
    HighlightType(final Color color) {
        this.color = color;
    }


    /**
     * Returns the {@link Color} of this highlight type.
     *
     * @return the {@link Color} of this highlight type
     */
    public Color getColor() {
        return color;
    }
}
