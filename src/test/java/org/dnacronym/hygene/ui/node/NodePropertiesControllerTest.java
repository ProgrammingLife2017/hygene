package org.dnacronym.hygene.ui.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.ui.AbstractUITest;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link NodePropertiesController}.
 */
final class NodePropertiesControllerTest extends AbstractUITest {
    private NodePropertiesController nodePropertiesController;
    private GraphVisualizer graphVisualizer;


    @Override
    public void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);

        nodePropertiesController = new NodePropertiesController();
        nodePropertiesController.setGraphVisualiser(graphVisualizer);
    }


    @Test
    void testFocusAction() {
        final Node node = mock(Node.class);
        final ObjectProperty<Node> selectedNodeProperty = new SimpleObjectProperty<>(node);
        final IntegerProperty centerNodeIdProperty = mock(IntegerProperty.class);

        when(node.getId()).thenReturn(10);
        when(graphVisualizer.getSelectedNodeProperty()).thenReturn(selectedNodeProperty);
        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);

        interact(() -> nodePropertiesController.onFocusAction(mock(ActionEvent.class)));

        verify(centerNodeIdProperty, times(1)).set(10);
    }
}
