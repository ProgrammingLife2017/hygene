package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;


/**
 * Unit tests of {@link AbstractColorSchemeMinMax}.
 */
abstract class ColorSchemeMinMaxBase {
    private final Color minColor = Color.BLUE;
    private final Color maxColor = Color.ANTIQUEWHITE;

    /**
     * Gets the minimum {@link Color}.
     *
     * @return the minimum {@link Color}
     */
    final Color getMinColor() {
        return minColor;
    }

    /**
     * Gets the maximum {@link Color}.
     *
     * @return the maximum {@link Color}
     */
    final Color getMaxColor() {
        return maxColor;
    }
}
