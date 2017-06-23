package org.dnacronym.hygene.ui.graph;

import javafx.scene.paint.Color;


/**
 * Class that will sequentially generate a quantitative color scheme.
 */
public final class ColorRoulette {
    private int index = -1;
    private int lightIndex = -1;


    /**
     * Retrieves the next {@link Color}.
     *
     * @return the {@link Color}
     */
    public synchronized Color getNext() {
        final DarkQuantitativeColors[] colors = DarkQuantitativeColors.values();
        index = (index + 1) % colors.length;

        return colors[index].color;
    }

    /**
     * Retrieves the next light quantitative {@link Color}.
     *
     * @return the {@link Color}
     */
    public synchronized Color getNextLightColor() {
        final LightQuantitativeColors[] colors = LightQuantitativeColors.values();
        lightIndex++;
        lightIndex = lightIndex % colors.length;

        return colors[lightIndex].color;
    }


    /**
     * Enum representing a color scheme containing a dark quantitative color scheme.
     */
    enum DarkQuantitativeColors {
        DARK_BLUE(Color.rgb(31, 120, 180)),
        DARK_GREEN(Color.rgb(51, 160, 44)),
        RED(Color.rgb(227, 26, 28)),
        PURPLE(Color.rgb(106, 61, 154)),
        BROWN(Color.rgb(177, 89, 40)),
        OCHRE(Color.rgb(253, 191, 111)),
        ORANGE(Color.rgb(255, 127, 0)),
        LIGHT_GREEN(Color.rgb(178, 223, 138)),
        VIOLET(Color.rgb(202, 178, 214)),
        LIGHT_BLUE(Color.rgb(166, 206, 227)),
        PINK(Color.rgb(251, 154, 153)),
        YELLOW(Color.rgb(255, 255, 153));

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

    /**
     * Enum representing a color scheme containing a light quantitative color scheme.
     */
    enum LightQuantitativeColors {
        SWAMP_GREEN(Color.rgb(141, 211, 199)),
        LIGHT_YELLOW(Color.rgb(255, 255, 179)),
        VIOLET(Color.rgb(190, 186, 218)),
        RED(Color.rgb(251, 128, 114)),
        BLUE(Color.rgb(128, 177, 211)),
        ORANGE(Color.rgb(253, 180, 98)),
        GREEN(Color.rgb(179, 222, 105)),
        PINK(Color.rgb(252, 205, 229)),
        GRAY(Color.rgb(217, 217, 217)),
        PURPLE(Color.rgb(188, 128, 189)),
        LIGHT_GREEN(Color.rgb(204, 235, 197)),
        YELLOW(Color.rgb(255, 237, 111));

        private Color color;


        /**
         * Constructs a new {@link DarkQuantitativeColors} instance.
         *
         * @param color the color
         */
        LightQuantitativeColors(final Color color) {
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
