package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.NewNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeIncomingEdges}.
 */
final class ColorSchemeIncomingEdgesTest {
    private ColorSchemeIncomingEdges colorSchemeIncomingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeIncomingEdges = new ColorSchemeIncomingEdges(10, Color.BLACK, Color.GREEN);
    }


    @Test
    @SuppressWarnings("unchecked")
    void testIncomingEdgesColor() {
        final NewNode node = mock(NewNode.class);
        final Set incoming = mock(Set.class);
        when(incoming.size()).thenReturn(3);
        when(node.getIncomingEdges()).thenReturn(incoming);

        assertThat(colorSchemeIncomingEdges.calculateColor(node)).isEqualTo(
                Color.BLACK.interpolate(Color.GREEN, 3.0 / 10)
        );
    }
}
