package org.dnacronym.hygene.ui.controller.leftpane;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.dnacronym.hygene.ui.visualizer.NeighbourVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the node properties window. Shows the properties of the selected node.
 */
public final class NodePropertiesController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(NodePropertiesController.class);

    private GraphVisualizer graphVisualizer;

    @FXML
    private TextField sequence;
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
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize NodePropertiesController.", e);
            return;
        }

        final ObjectProperty<Node> selectedNodeProperty = graphVisualizer.getSelectedNodeProperty();

        final NeighbourVisualizer neighbourVisualizer
                = new NeighbourVisualizer(graphVisualizer.getEdgeColorProperty(), selectedNodeProperty);
        neighbourVisualizer.setCanvas(neighbourCanvas);

        selectedNodeProperty.addListener((observable, oldNode, newNode) -> {
            if (newNode == null) {
                return;
            }

            try {
                sequence.setText(newNode.retrieveMetadata().getSequence());
            } catch (final ParseException e) {
                LOGGER.error("Error when parsing a node.", e);
            }

            leftNeighbours.setText(String.valueOf(newNode.getNumberOfIncomingEdges()));
            rightNeighbours.setText(String.valueOf(newNode.getNumberOfOutgoingEdges()));

            position.setText(String.valueOf(newNode.getId()));
        });
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
}
