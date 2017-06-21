package org.dnacronym.hygene.ui.node;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the node properties window. Shows the properties of the selected node.
 */
public final class NodePropertiesController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(NodePropertiesController.class);

    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private TextField nodeId;
    @FXML
    private TextField sequencePreview;
    @FXML
    private Canvas neighbourCanvas;
    @FXML
    private TextField leftNeighbours;
    @FXML
    private TextField rightNeighbours;
    @FXML
    private TextField position;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<Segment> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();

        final NeighbourVisualizer neighbourVisualizer
                = new NeighbourVisualizer(graphVisualizer.getEdgeColorProperty(), selectedNodeProperty);
        neighbourVisualizer.setCanvas(neighbourCanvas);

        selectedNodeProperty.addListener((observable, oldNode, newNode) -> updateFields(newNode));
    }

    /**
     * Set the {@link GraphVisualizer}, whose selected node can be bound to the UI elements in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} who's selected node we are interested in
     * @see GraphVisualizer#selectedNodeProperty
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Sets the {@link GraphDimensionsCalculator} for use by the controller.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} for use by the controller
     */
    void setGraphDimensionsCalculator(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
    }

    /**
     * Updates the fields that describe the properties of the {@link Segment}.
     * <p>
     * If this {@link Segment} is {@code null}, the fields are simply cleared.
     *
     * @param node the {@link Segment} whose properties should be displayed
     */
    void updateFields(final Segment node) {
        if (node == null) {
            clearNodeFields();
            return;
        }

        nodeId.setText(String.valueOf(node.getId()));

        if (node.hasMetadata()) {
            sequencePreview.setText(String.valueOf(node.getMetadata().getSequence()));
        } else {
            LOGGER.error("Unable to parse sequence of node " + node.getId() + ".");
        }

        leftNeighbours.setText(String.valueOf(node.getIncomingEdges().size()));
        rightNeighbours.setText(String.valueOf(node.getOutgoingEdges().size()));

        position.setText(String.valueOf(node.getId()));
    }


    /**
     * Clear all text fields used to describe node properties.
     */
    private void clearNodeFields() {
        nodeId.clear();
        sequencePreview.clear();
        leftNeighbours.clear();
        rightNeighbours.clear();
        position.clear();
    }

    /**
     * When the user clicks on the focus {@link javafx.scene.control.Button}.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onFocusAction(final ActionEvent actionEvent) {
        final Segment selectedNode = graphVisualizer.getSelectedSegmentProperty().get();
        if (selectedNode != null) {
            graphDimensionsCalculator.getCenterNodeIdProperty().set(selectedNode.getId());
        }

        actionEvent.consume();
    }
}
