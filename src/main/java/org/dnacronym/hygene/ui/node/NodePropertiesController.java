package org.dnacronym.hygene.ui.node;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.node.GfaNode;
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
    @Inject
    private SequenceVisualizer sequenceVisualizer;

    @FXML
    private Label nodeId;
    @FXML
    private Label sequencePreview;
    @FXML
    private Label leftNeighbours;
    @FXML
    private Label rightNeighbours;
    @FXML
    private Label position;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<GfaNode> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();


        selectedNodeProperty.addListener((observable, oldNode, newNode) -> updateFields(newNode));
    }

    /**
     * Set the {@link GraphVisualizer}, whose selected node can be bound to the UI elements in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} who's selected node we are interested in
     * @see GraphVisualizer#selectedSegmentProperty
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
     * Updates the fields that describe the properties of the {@link GfaNode}.
     * <p>
     * If this {@link GfaNode} is {@code null}, the fields are simply cleared.
     *
     * @param node the {@link GfaNode} whose properties should be displayed
     */
    void updateFields(final GfaNode node) {
        if (node == null) {
            clearNodeFields();
            return;
        }

        if (node.hasMetadata()) {
            nodeId.setText(node.getMetadata().getName());
            sequencePreview.setText(String.valueOf(node.getMetadata().getSequence()));
        } else {
            LOGGER.error("Node " + node.getSegmentIds().toString() + " does not have metadata.");
        }

        leftNeighbours.setText(String.valueOf(node.getIncomingEdges().size()));
        rightNeighbours.setText(String.valueOf(node.getOutgoingEdges().size()));

        position.setText(node.getSegmentIds().toString());
    }


    /**
     * Clear all text fields used to describe node properties.
     */
    private void clearNodeFields() {
        nodeId.setText("");
        sequencePreview.setText("");
        leftNeighbours.setText("");
        rightNeighbours.setText("");
        position.setText("");
    }

    /**
     * When the user clicks on the focus {@link javafx.scene.control.Button}.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onFocusAction(final ActionEvent actionEvent) {
        final GfaNode selectedNode = graphVisualizer.getSelectedSegmentProperty().get();
        if (selectedNode != null) {
            graphDimensionsCalculator.getCenterNodeIdProperty().set(selectedNode.getSegmentIds().get(0));
        }

        actionEvent.consume();
    }

    /**
     * When the user clicks on the view sequence {@link javafx.scene.control.Button}.
     *
     * @param event the {@link MouseEvent}
     */
    @FXML
    void onViewSequence(final MouseEvent event) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        event.consume();
    }
}
