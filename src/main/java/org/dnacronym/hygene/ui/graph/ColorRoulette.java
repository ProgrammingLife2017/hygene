package org.dnacronym.hygene.ui.graph;

import javafx.scene.paint.Color;

/**
 * Class that will sequentially generate a quantitative color scheme.
 */
public class ColorRoulette {
    private int index = -1;

    /**
     * Retrieve the next color.
     */
    public synchronized Color getNext() {
        DarkQuantitativeColors[] colors = DarkQuantitativeColors.values();
        index = index++ % colors.length;

        return colors[index].color;
    }

    /**
     * Enum representing a color scheme containing a dark quantitative color scheme.
     */
    enum DarkQuantitativeColors {
        LIGHT_BLUE(Color.color(166, 206, 227)),
        DARK_BLUE(Color.color(31, 120, 180)),
        LIGHT_GREEN(Color.color(178, 223, 138)),
        DARK_GREEN(Color.color(51, 160, 44)),
        PINK(Color.color(251, 154, 153)),
        RED(Color.color(227, 26, 28)),
        UNHEALTHLY_DIARREA(Color.color(253, 191, 111)),
        ORANGE(Color.color(255, 127, 0)),
        VIOLET(Color.color(202, 178, 214)),
        PURPLE(Color.color(106, 61, 154)),
        YELLOW(Color.color(255, 255, 153)),
        BROWN(Color.color(177, 89, 40));

        private Color color;


        /**
         * Construct a new {@link DarkQuantitativeColors} instance.
         *
         * @param color the color
         */
        DarkQuantitativeColors(final Color color) {
            this.color = color;
        }
    }
}
