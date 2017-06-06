package org.dnacronym.hygene.models.colorscheme.minmax;

import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeIncomingEdges}.
 */
final class ColorSchemeIncomingEdgesTest extends ColorSchemeMinMaxBase {
    private ColorSchemeIncomingEdges colorSchemeIncomingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeIncomingEdges = new ColorSchemeIncomingEdges();
        colorSchemeIncomingEdges.setMinColor(getMinColor());
        colorSchemeIncomingEdges.setMaxColor(getMaxColor());
    }


    @Test
    void testDefaultMaxValue() {
        assertThat(colorSchemeIncomingEdges.getMaxValue()).isEqualTo(5);
    }

    @Test
    void testIncomingEdgesColor() {
        final Node node = mock(Node.class);
        when(node.getNumberOfIncomingEdges()).thenReturn(3);

        assertThat(colorSchemeIncomingEdges.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 3.0 / colorSchemeIncomingEdges.getMaxValue())
        );
    }
}
