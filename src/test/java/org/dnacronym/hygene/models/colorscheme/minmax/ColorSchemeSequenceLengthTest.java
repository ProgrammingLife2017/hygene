package org.dnacronym.hygene.models.colorscheme.minmax;

import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeSequenceLength}.
 */
final class ColorSchemeSequenceLengthTest extends ColorSchemeMinMaxBase {
    private ColorSchemeSequenceLength colorSchemeSequenceLength;


    @BeforeEach
    void beforeEach() {
        colorSchemeSequenceLength = new ColorSchemeSequenceLength();
        colorSchemeSequenceLength.setMinColor(getMinColor());
        colorSchemeSequenceLength.setMaxColor(getMaxColor());
    }


    @Test
    void testDefaultMaxValue() {
        assertThat(colorSchemeSequenceLength.getMaxValue()).isEqualTo(100);
    }

    @Test
    void testSequenceLengthColor() {
        final Node node = mock(Node.class);
        when(node.getSequenceLength()).thenReturn(78);

        assertThat(colorSchemeSequenceLength.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 78.0 / colorSchemeSequenceLength.getMaxValue())
        );
    }
}
