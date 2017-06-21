package org.dnacronym.hygene.ui.graph;

import javafx.scene.paint.Color;


/**
 * Class that will sequentially generate a quantitative color scheme.
 */
public final class ColorRoulette {
    private int index = -1;


    /**
     * Retrieves the next {@link Color}.
     *
     * @return the {@link Color}
     */
    public synchronized Color getNext() {
        final DarkQuantitativeColors[] colors = DarkQuantitativeColors.values();
        index = ++index % colors.length;

        return colors[index].color;
    }


    /**
     * Enum representing a color scheme containing a dark quantitative color scheme.
     */
    enum DarkQuantitativeColors {
        LIGHT_BLUE(Color.rgb(166, 206, 227)),
        DARK_BLUE(Color.rgb(31, 120, 180)),
        LIGHT_GREEN(Color.rgb(178, 223, 138)),
        DARK_GREEN(Color.rgb(51, 160, 44)),
        PINK(Color.rgb(251, 154, 153)),
        RED(Color.rgb(227, 26, 28)),
        UNHEALTHLY_DIARREA(Color.rgb(253, 191, 111)),
        ORANGE(Color.rgb(255, 127, 0)),
        VIOLET(Color.rgb(202, 178, 214)),
        PURPLE(Color.rgb(106, 61, 154)),
        YELLOW(Color.rgb(255, 255, 153)),
        BROWN(Color.rgb(177, 89, 40));

        private Color color;


        /**
         * Constructs a new {@link DarkQuantitativeColors} instance.
         *
         * @param color the color
         */
        DarkQuantitativeColors(final Color color) {
            this.color = color;
        }


        /**
         * Gets the {@link Color}.
         *
         * @return the {@link Color}
         */
        public Color getColor() {
            return color;
        }
    }
}
