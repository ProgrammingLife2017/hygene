package org.dnacronym.hygene.ui.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link NodePropertiesController}.
 */
final class NodePropertiesControllerTest extends UITestBase {
    private NodePropertiesController nodePropertiesController;
    private GraphVisualizer graphVisualizer;
    private Node node;
    private GraphDimensionsCalculator graphDimensionsCalculator;


    @Override
    public void beforeEach() {
        graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);
        graphVisualizer = mock(GraphVisualizer.class);
        node = mock(Node.class);
        when(node.getId()).thenReturn(20);
        final ObjectProperty<Node> selectedNodeProperty = new SimpleObjectProperty<>(node);
        when(graphVisualizer.getSelectedSegmentProperty()).thenReturn(selectedNodeProperty);

        nodePropertiesController = new NodePropertiesController();
        nodePropertiesController.setGraphDimensionsCalculator(graphDimensionsCalculator);
        nodePropertiesController.setGraphVisualiser(graphVisualizer);
    }


    @Test
    void testFocusAction() {
        final IntegerProperty centerNodeIdProperty = new SimpleIntegerProperty(-1);
        when(graphDimensionsCalculator.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);

        interact(() -> nodePropertiesController.onFocusAction(mock(ActionEvent.class)));

        assertThat(centerNodeIdProperty.get()).isEqualTo(20);
    }
}
