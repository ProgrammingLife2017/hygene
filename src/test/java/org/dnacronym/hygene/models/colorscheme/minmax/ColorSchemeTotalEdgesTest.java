package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeTotalEdges}.
 */
final class ColorSchemeTotalEdgesTest {
    private ColorSchemeTotalEdges colorSchemeTotalEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeTotalEdges = new ColorSchemeTotalEdges(44, Color.BROWN, Color.CORNSILK);
    }


    @Test
    void testCalculateColorTotalEdges() {
        final Node node = mock(Node.class);
        when(node.getNumberOfIncomingEdges()).thenReturn(4);
        when(node.getNumberOfOutgoingEdges()).thenReturn(2);

        assertThat(colorSchemeTotalEdges.calculateColor(node)).isEqualTo(
                Color.BROWN.interpolate(Color.CORNSILK, 6.0 / colorSchemeTotalEdges.getMaxValue())
        );
    }
}
