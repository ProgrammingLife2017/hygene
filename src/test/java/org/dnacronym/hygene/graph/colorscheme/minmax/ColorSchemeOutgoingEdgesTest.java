package org.dnacronym.hygene.graph.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeOutgoingEdges}.
 */
final class ColorSchemeOutgoingEdgesTest {
    private ColorSchemeOutgoingEdges colorSchemeOutgoingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeOutgoingEdges = new ColorSchemeOutgoingEdges(32, Color.AZURE, Color.BURLYWOOD);
    }


    @Test
    @SuppressWarnings("unchecked")
    void testOutgoingEdgesColor() {
        final Node node = mock(Node.class);
        final Set outgoing = mock(Set.class);
        when(outgoing.size()).thenReturn(16);
        when(node.getOutgoingEdges()).thenReturn(outgoing);

        assertThat(colorSchemeOutgoingEdges.calculateColor(node)).isEqualTo(
                Color.AZURE.interpolate(Color.BURLYWOOD, 16.0 / 32)
        );
    }
}
