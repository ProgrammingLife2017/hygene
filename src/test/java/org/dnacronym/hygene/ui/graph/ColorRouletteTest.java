package org.dnacronym.hygene.ui.graph;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit test for {@link ColorRoulette}.
 */
class ColorRouletteTest {
    private ColorRoulette colorRoulette;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        colorRoulette = new ColorRoulette();
    }


    @Test
    void getNext() {
        final Color color = colorRoulette.getNext();

        assertThat(color).isEqualTo(ColorRoulette.DarkQuantitativeColors.LIGHT_BLUE.getColor());
    }
}
