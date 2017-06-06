package org.dnacronym.hygene.models.colorscheme.minmax;

import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeOutgoingEdges}.
 */
final class ColorSchemeOutgoingEdgesTest extends ColorSchemeMinMaxBase {
    private ColorSchemeOutgoingEdges colorSchemeOutgoingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeOutgoingEdges = new ColorSchemeOutgoingEdges();
        colorSchemeOutgoingEdges.setMinColor(getMinColor());
        colorSchemeOutgoingEdges.setMaxColor(getMaxColor());
    }


    @Test
    void testDefaultMaxValue() {
        assertThat(colorSchemeOutgoingEdges.getMaxValue()).isEqualTo(5);
    }

    @Test
    void testOutgoingEdgesColor() {
        final Node node = mock(Node.class);
        when(node.getNumberOfOutgoingEdges()).thenReturn(3);

        assertThat(colorSchemeOutgoingEdges.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 3.0 / colorSchemeOutgoingEdges.getMaxValue())
        );
    }
}
