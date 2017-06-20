package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link FillNode} class.
 */
final class FillNodeTest extends NodeTest {
    private FillNode fillNode;


    @BeforeEach
    void setUp() {
        fillNode = new FillNode();
        setNode(fillNode);
    }


    @Test
    void testFillNodeHasNoLength() {
        assertThat(fillNode.getLength()).isEqualTo(0);
    }

    @Test
    void testThatMetadataIsNotSupported() {
        final Throwable getMetadataException = catchThrowable(() -> fillNode.getMetadata());
        assertThat(getMetadataException).isInstanceOf(UnsupportedOperationException.class);

        final Throwable setMetadataException = catchThrowable(() -> fillNode.setMetadata(mock(NodeMetadata.class)));
        assertThat(setMetadataException).isInstanceOf(UnsupportedOperationException.class);

        assertThat(fillNode.hasMetadata()).isFalse();
    }
}
