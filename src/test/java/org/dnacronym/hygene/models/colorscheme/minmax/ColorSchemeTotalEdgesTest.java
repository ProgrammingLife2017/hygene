package org.dnacronym.hygene.models.colorscheme.minmax;

import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeTotalEdges}.
 */
final class ColorSchemeTotalEdgesTest extends ColorSchemeMinMaxBase {
    private ColorSchemeTotalEdges colorSchemeTotalEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeTotalEdges = new ColorSchemeTotalEdges();
        colorSchemeTotalEdges.setMinColor(getMinColor());
        colorSchemeTotalEdges.setMaxColor(getMaxColor());
    }


    @Test
    void testDefaultMaxValue() {
        assertThat(colorSchemeTotalEdges.getMaxValue()).isEqualTo(10);
    }

    @Test
    void testCalculateColorTotalEdges() {
        final Node node = mock(Node.class);
        when(node.getNumberOfIncomingEdges()).thenReturn(4);
        when(node.getNumberOfOutgoingEdges()).thenReturn(2);

        assertThat(colorSchemeTotalEdges.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 6.0 / colorSchemeTotalEdges.getMaxValue())
        );
    }
}
