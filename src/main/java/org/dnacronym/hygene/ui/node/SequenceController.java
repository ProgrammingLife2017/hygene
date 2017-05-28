package org.dnacronym.hygene.ui.node;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the sequence view.
 */
public class SequenceController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(SequenceController.class);

    private SequenceVisualizer sequenceVisualizer;
    private GraphVisualizer graphVisualizer;

    @FXML
    private Pane sequenceViewPane;
    @FXML
    private TextField lengthField;
    @FXML
    private Canvas sequenceCanvas;
    @FXML
    private Slider incrementAmount;


    /**
     * Create instance of {@link SequenceController}.
     */
    public SequenceController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setSequenceVisualizer(Hygene.getInstance().getSequenceVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sequenceVisualizer.setCanvas(sequenceCanvas);

        graphVisualizer.getSelectedNodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                lengthField.clear();
                sequenceVisualizer.getSequenceProperty().set(null);
                return;
            }

            lengthField.setText(String.valueOf(newValue.getSequenceLength()));

            try {
                sequenceVisualizer.getSequenceProperty().set(newValue.retrieveMetadata().getSequence());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse metadata of node %s for sequence visualisation.", newValue, e);
            }
        });

        sequenceViewPane.visibleProperty().bind(sequenceVisualizer.getVisibleProperty());
        sequenceViewPane.managedProperty().bind(sequenceVisualizer.getVisibleProperty());
    }

    /**
     * Sets the {@link GraphVisualizer} for use by the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} for use by the controller
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
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
     * When the user wants to move by only a single amount.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void incrementSmallAction(final ActionEvent actionEvent) {
        sequenceVisualizer.incrementOffset(1);
        actionEvent.consume();
    }

    /**
     * When the user wants to increment by a large amount.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void incrementLargeAction(final ActionEvent actionEvent) {
        sequenceVisualizer.incrementOffset((int) incrementAmount.getValue());
        actionEvent.consume();
    }

    /**
     * When the user wants to decrement by a small amount.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void decrementSmallAction(final ActionEvent actionEvent) {
        sequenceVisualizer.decrementOffset(1);
        actionEvent.consume();
    }

    /**
     * When the user wants to decrement by a large amount.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void decrementLargeAction(final ActionEvent actionEvent) {
        sequenceVisualizer.decrementOffset((int) incrementAmount.getValue());
        actionEvent.consume();
    }
}
