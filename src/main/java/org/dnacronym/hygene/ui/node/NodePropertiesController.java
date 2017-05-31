package org.dnacronym.hygene.ui.node;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the node properties window. Shows the properties of the selected node.
 */
public final class NodePropertiesController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(NodePropertiesController.class);

    private SequenceVisualizer sequenceVisualizer;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphVisualizer graphVisualizer;

    @FXML
    private Pane nodePropertiesPane;
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
    @FXML
    private ToggleButton viewSequence;


    /**
     * Create instance of {@link NodePropertiesController}.
     */
    public NodePropertiesController() {
        try {
            setSequenceVisualizer(Hygene.getInstance().getSequenceVisualizer());
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
            setGraphDimensionsCalculator(Hygene.getInstance().getGraphDimensionsCalculator());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize NodePropertiesController.", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<Node> selectedNodeProperty = graphVisualizer.getSelectedNodeProperty();

        final NeighbourVisualizer neighbourVisualizer
                = new NeighbourVisualizer(graphVisualizer.getEdgeColorProperty(), selectedNodeProperty);
        neighbourVisualizer.setCanvas(neighbourCanvas);

        selectedNodeProperty.addListener((observable, oldNode, newNode) -> updateFields(newNode));

        nodePropertiesPane.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        nodePropertiesPane.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        sequenceVisualizer.getVisibleProperty().bind(Bindings.and(
                graphVisualizer.getSelectedNodeProperty().isNotNull(),
                viewSequence.selectedProperty()));
    }

    /**
     * Sets the {@link SequenceVisualizer} for use by the controller.
     *
     * @param sequenceVisualizer {@link SequenceVisualizer} for use by the controller
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
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
     * Updates the fields that describe the properties of the {@link Node}.
     * <p>
     * If this {@link Node} is {@code null}, the fields are simply cleared.
     *
     * @param node the {@link Node} whose properties should be displayed
     */
    private void updateFields(final Node node) {
        if (node == null) {
            clearNodeFields();
            return;
        }

        nodeId.setText(String.valueOf(node.getId()));

        try {
            sequencePreview.setText(String.valueOf(node.retrieveMetadata().getSequence()));
        } catch (final ParseException e) {
            LOGGER.error("Unable to parse sequence of node %s.", node, e);
        }

        leftNeighbours.setText(String.valueOf(node.getNumberOfIncomingEdges()));
        rightNeighbours.setText(String.valueOf(node.getNumberOfOutgoingEdges()));

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
        final Node selectedNode = graphVisualizer.getSelectedNodeProperty().get();
        if (selectedNode != null) {
            graphDimensionsCalculator.getCenterNodeIdProperty().set(selectedNode.getId());
        }

        actionEvent.consume();
    }
}
