package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for creating bookmarks.
 */
public final class BookmarkCreateController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BookmarkCreateController.class);

    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphVisualizer graphVisualizer;
    private SequenceVisualizer sequenceVisualizer;
    private SimpleBookmarkStore simpleBookmarkStore;

    @FXML
    private TextField baseOffset;
    @FXML
    private TextField radius;
    @FXML
    private TextArea description;
    @FXML
    private Button save;


    /**
     * Create instance of {@link BookmarkCreateController}.
     */
    public BookmarkCreateController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setSequenceVisualizer(Hygene.getInstance().getSequenceVisualizer());
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setGraphDimensionsCalculator(Hygene.getInstance().getGraphDimensionsCalculator());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<Segment> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();

        baseOffset.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        baseOffset.setText(String.valueOf(sequenceVisualizer.getOffsetProperty().get()));
        baseOffset.textProperty().addListener((observable, oldValue, newValue) -> updateBaseOffset(newValue));
        sequenceVisualizer.getOffsetProperty().addListener((observable, oldValue, newValue) ->
                baseOffset.setText(String.valueOf(newValue)));

        radius.setText(String.valueOf(graphDimensionsCalculator.getRadiusProperty().get()));
        graphDimensionsCalculator.getRadiusProperty().addListener((observable, oldValue, newValue) ->
                radius.setText(String.valueOf(newValue)));

        baseOffset.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        radius.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        description.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        save.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
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
     * @param sequenceVisualizer {@link SequenceVisualizer} for use by the controller.
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
    }

    /**
     * Set the {@link GraphDimensionsCalculator} in the controller.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} for use by the controller
     */
    void setGraphDimensionsCalculator(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
    }

    /**
     * Sets the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
    }

    /**
     * Update the current base offset in the sequence visualizer and the base offset {@link TextField}.
     * <p>
     * If the base offset string is {@code null} or empty, the offset in the {@link SequenceVisualizer} is set to 0 and
     * the text of the base offset {@link TextField} is set to "0".
     *
     * @param offset the new offset string
     */
    void updateBaseOffset(final String offset) {
        if (offset == null || offset.isEmpty()) {
            sequenceVisualizer.setOffset(0);
            baseOffset.setText("0");
            return;
        }

        sequenceVisualizer.setOffset(Integer.parseInt(offset));
        baseOffset.setText(sequenceVisualizer.getOffsetProperty().asString().get());
    }

    /**
     * When the user saves the bookmark.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onSaveAction(final ActionEvent actionEvent) {
        if (graphVisualizer.getSelectedSegmentProperty().isNull().get()) {
            return;
        }

        final String baseString = baseOffset.getText().replaceAll("[^\\d]", "");
        final String radiusString = radius.getText().replaceAll("[^\\d]", "");

        if (!baseString.isEmpty() && !radiusString.isEmpty()) {
            final Segment segment = graphVisualizer.getSelectedSegmentProperty().get();
            final int baseOffsetValue = Integer.parseInt(baseString);
            final int radiusValue = Integer.parseInt(radiusString);

            simpleBookmarkStore.addBookmark(new Bookmark(segment.getId(), baseOffsetValue, radiusValue,
                    description.getText()));
            description.clear();
        }

        actionEvent.consume();
    }
}
