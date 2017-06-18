package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.NewNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
    @SuppressWarnings("unchecked")
    void testCalculateColorTotalEdges() {
        final NewNode node = mock(NewNode.class);
        final Set incoming = mock(Set.class);
        when(incoming.size()).thenReturn(4);
        final Set outgoing = mock(Set.class);
        when(outgoing.size()).thenReturn(16);
        when(node.getIncomingEdges()).thenReturn(incoming);
        when(node.getOutgoingEdges()).thenReturn(outgoing);

        assertThat(colorSchemeTotalEdges.calculateColor(node)).isEqualTo(
                Color.BROWN.interpolate(Color.CORNSILK, 20.0 / 44)
        );
    }
}
