package org.dnacronym.hygene.models.colorscheme.fixed;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;
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
        fixedColor = new FixedColorScheme(Color.ALICEBLUE);
    }


    @Test
    void testSetFixedColor() {
        assertThat(fixedColor.calculateColor(mock(NewNode.class))).isEqualTo(Color.ALICEBLUE);
    }
}
