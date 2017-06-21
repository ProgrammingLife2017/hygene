package org.dnacronym.hygene.ui.node;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the sequence view.
 */
public final class SequenceController implements Initializable {
    private static final int CANVAS_PADDING = 10;

    @Inject
    private SequenceVisualizer sequenceVisualizer;
    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphStore graphStore;

    @FXML
    private TitledPane sequenceViewPane;
    @FXML
    private GridPane sequenceGrid;
    @FXML
    private TextField lengthField;
    @FXML
    private Canvas sequenceCanvas;
    @FXML
    private TextField setOffset;
    @FXML
    private TextArea sequenceTextArea;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        sequenceVisualizer.setCanvas(sequenceCanvas);

        setOffset.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        sequenceVisualizer.getOffsetProperty().addListener((observable, oldValue, newValue) -> {
            setOffset.setText(String.valueOf(newValue));

            sequenceTextArea.positionCaret(newValue.intValue());
            sequenceTextArea.selectPositionCaret(newValue.intValue() + 1);
        });

        graphVisualizer.getSelectedSegmentProperty()
                .addListener((observable, oldNode, newNode) -> updateFields(newNode));
        sequenceCanvas.widthProperty().bind(sequenceGrid.widthProperty().subtract(CANVAS_PADDING * 2));

        sequenceViewPane.visibleProperty().bind(sequenceVisualizer.getVisibleProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        sequenceViewPane.managedProperty().bind(sequenceVisualizer.getVisibleProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
    }

    /**
     * Sets the {@link SequenceVisualizer} for use by the controller.
     *
     * @param sequenceVisualizer the {@link SequenceVisualizer} for use by the controller
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
    }

    /**
     * Updates the fields that describe the sequence of the {@link GfaNode}.
     * <p>
     * If this {@link GfaNode} is {@code null}, the fields are simply cleared.
     *
     * @param node the {@link GfaNode} whose sequence properties should be displayed
     */
    void updateFields(final GfaNode node) {
        if (!(node instanceof Segment)) { // Or equals null
            lengthField.clear();
            sequenceTextArea.clear();
            sequenceVisualizer.getSequenceProperty().set(null);
            return;
        }

        final Segment segment = (Segment) node;
        lengthField.setText(String.valueOf(segment.getSequenceLength()));
        setOffset.setPromptText("0 - " + (segment.getSequenceLength() - 1));

        if (segment.hasMetadata()) {
            final String sequence = segment.getMetadata().getSequence();
            sequenceVisualizer.getSequenceProperty().set(sequence);
            sequenceTextArea.setText(sequence);
        }
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
        sequenceVisualizer.incrementOffset(sequenceVisualizer.getOnScreenBasesCountProperty().get());
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
        sequenceVisualizer.decrementOffset(sequenceVisualizer.getOnScreenBasesCountProperty().get());
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
        setOffset.setText(String.valueOf(sequenceVisualizer.getOffsetProperty().get()));

        actionEvent.consume();
    }

    /**
     * When the user wants to set the offset to the selected base in the text area.
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
