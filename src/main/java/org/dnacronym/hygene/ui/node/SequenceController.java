package org.dnacronym.hygene.ui.node;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.util.converter.IntegerStringConverter;
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
public final class SequenceController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(SequenceController.class);
    private static final int CANVAS_PADDING = 10;

    private SequenceVisualizer sequenceVisualizer;
    private GraphVisualizer graphVisualizer;

    @FXML
    private Pane sequenceViewPane;
    @FXML
    private TextField lengthField;
    @FXML
    private Canvas sequenceCanvas;
    @FXML
    private TextField setOffset;
    @FXML
    private TextArea sequenceTextArea;


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
    public void initialize(final URL location, final ResourceBundle resources) {
        sequenceVisualizer.setCanvas(sequenceCanvas);

        setOffset.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        sequenceVisualizer.getOffsetProperty().addListener((observable, oldValue, newValue) -> {
            setOffset.setText(String.valueOf(newValue));

            sequenceTextArea.positionCaret(newValue.intValue());
            sequenceTextArea.selectPositionCaret(newValue.intValue() + 1);
        });

        graphVisualizer.getSelectedNodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                lengthField.clear();
                sequenceTextArea.clear();
                return;
            }

            lengthField.setText(String.valueOf(newValue.getSequenceLength()));
            setOffset.setPromptText("0 - " + (newValue.getSequenceLength() - 1));

            try {
                final String sequence = newValue.retrieveMetadata().getSequence();
                sequenceVisualizer.getSequenceProperty().set(sequence);
                sequenceTextArea.setText(sequence);
            } catch (ParseException e) {
                LOGGER.error("Unable to parse metadata of node %s for sequence visualisation.", newValue, e);
            }
        });

        final BooleanBinding visible = Bindings.and(
                Bindings.isNotNull(graphVisualizer.getSelectedNodeProperty()),
                sequenceVisualizer.getVisibleProperty());
        sequenceViewPane.visibleProperty().

                bind(visible);
        sequenceViewPane.managedProperty().

                bind(visible);

        sequenceCanvas.widthProperty().

                bind(Bindings.subtract(sequenceViewPane.widthProperty(), CANVAS_PADDING * 2));
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
        sequenceVisualizer.incrementOffset(sequenceVisualizer.onScreenBasesCountProperty().get());
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
        sequenceVisualizer.decrementOffset(sequenceVisualizer.onScreenBasesCountProperty().get());
        actionEvent.consume();
    }

    /**
     * When the user sets a new offset.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void setOffsetAction(final ActionEvent actionEvent) {
        if (!setOffset.getText().isEmpty()) {
            sequenceVisualizer.setOffset(Integer.parseInt(setOffset.getText()));
        }

        actionEvent.consume();
    }

    /**
     * When the user wants to set the offset to the selected base in the textarea.
     * <p>
     * The offset is based on the caret position.
     *
     * @param actionEvent the {@link ActionEvent}
     * @see TextArea#caretPosition
     */
    @FXML
    void getTextAreaOffsetAction(final ActionEvent actionEvent) {
        sequenceVisualizer.setOffset(sequenceTextArea.getCaretPosition() - sequenceTextArea.getSelectedText().length());

        actionEvent.consume();
    }

    /**
     * When the user wants to go to the start of the sequence.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goToStartAction(final ActionEvent actionEvent) {
        sequenceVisualizer.setOffset(0);

        actionEvent.consume();
    }

    /**
     * When the user wants to go to the end of the sequence.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goToEndAction(final ActionEvent actionEvent) {
        if (sequenceVisualizer.getSequenceProperty().get() != null) {
            sequenceVisualizer.setOffset(sequenceVisualizer.getSequenceProperty().get().length() - 1);
        }

        actionEvent.consume();
    }
}
