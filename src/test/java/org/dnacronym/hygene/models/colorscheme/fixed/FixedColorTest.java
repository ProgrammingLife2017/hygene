package org.dnacronym.hygene.models.colorscheme.fixed;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for the {@link FixedColorScheme}.
 */
final class FixedColorTest {
    private FixedColorScheme fixedColor;


    @BeforeEach
    void beforeEach() {
        fixedColor = new FixedColorScheme();
    }


    @Test
    void testDefaultFixedColor() {
        assertThat(fixedColor.calculateColor(mock(Node.class))).isEqualTo(Color.PURPLE);
    }

    @Test
    void testSetFixedColor() {
        fixedColor.setColor(Color.ALICEBLUE);

        assertThat(fixedColor.calculateColor(mock(Node.class))).isEqualTo(Color.ALICEBLUE);
    }
}
