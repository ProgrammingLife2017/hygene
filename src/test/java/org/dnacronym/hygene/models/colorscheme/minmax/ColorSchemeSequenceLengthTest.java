package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeSequenceLength}.
 */
final class ColorSchemeSequenceLengthTest {
    private ColorSchemeSequenceLength colorSchemeSequenceLength;


    @BeforeEach
    void beforeEach() {
        colorSchemeSequenceLength = new ColorSchemeSequenceLength(120, Color.CHOCOLATE, Color.CYAN);
    }


    @Test
    void testSequenceLengthCap() {
        final NewNode node = mock(NewNode.class);
        when(node.getLength()).thenReturn(200);

        assertThat(colorSchemeSequenceLength.calculateColor(node)).isEqualTo(
                Color.CHOCOLATE.interpolate(Color.CYAN, 1)
        );
    }

    @Test
    void testSequenceLengthColor() {
        final NewNode node = mock(NewNode.class);
        when(node.getLength()).thenReturn(78);

        assertThat(colorSchemeSequenceLength.calculateColor(node)).isEqualTo(
                Color.CHOCOLATE.interpolate(Color.CYAN, 78.0 / 120)
        );
    }
}
