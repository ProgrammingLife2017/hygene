package org.dnacronym.hygene.ui.graph;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that will sequentially generate a quantitative color scheme.
 */
public class ColorRoulette {
    private List<Color> colors;
    int index = 0;

    /**
     * Construct a new Roulette
     */
    public ColorRoulette() {
        colors = new ArrayList<>();
    }

    /**
     * Retrieve the next color.
     */
    public synchronized Color getNext() {
        return QuantitativeColors.values()[index].color;
    }

    enum QuantitativeColors {
        LIGHT_BLUE(Color.color(166, 206, 227)),
        DARK_BLUE(Color.color(31, 120, 180)),
        LIGHT_GREEN(Color.color(178, 223, 138)),
        DARK_GREEN(Color.color(51, 160, 44)),
        PINK(Color.color(251, 154, 153)),
        RED(Color.color(227, 26, 28)),
        UNHEALTHLY_DIARREA(Color.color(253, 191, 111)),
        ORANGE(Color.color(255, , 127, 0)),
        VIOLET(Color.color(202, 178, 214)),
        PURPLE(Color.color(106, 61, 154)),
        YELLOW(Color.color(255, 255, 153)),
        BROWN(Color.color(177, 89, 40));

        private Color color;


        /**
         * Construct a new {@link QuantitativeColors} instance.
         *
         * @param color the color
         */
        QuantitativeColors(final Color color) {
            this.color = color;
        }
    }
}
