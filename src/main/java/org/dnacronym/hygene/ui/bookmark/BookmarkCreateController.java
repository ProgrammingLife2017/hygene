package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import org.dnacronym.hygene.graph.bookmark.Bookmark;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for creating bookmarks.
 */
public final class BookmarkCreateController implements Initializable {
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private SequenceVisualizer sequenceVisualizer;
    @Inject
    private BookmarkStore bookmarkStore;

    @FXML
    private Label nodePosition;
    @FXML
    private TextField baseOffset;
    @FXML
    private Label radius;
    @FXML
    private TextArea description;
    @FXML
    private Button saveButton;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<GfaNode> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();

        selectedNodeProperty.addListener((observable, oldValue, newValue) ->
                nodePosition.setText(newValue == null ? "" : String.valueOf(newValue.getSegmentIds())));
        radius.textProperty().bind(graphDimensionsCalculator.getRadiusProperty().asString());

        baseOffset.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        baseOffset.setText(String.valueOf(sequenceVisualizer.getOffsetProperty().get()));
        baseOffset.textProperty().addListener((observable, oldValue, newValue) -> updateBaseOffset(newValue));
        sequenceVisualizer.getOffsetProperty().addListener((observable, oldValue, newValue) ->
                baseOffset.setText(String.valueOf(newValue)));

        saveButton.disableProperty().bind(selectedNodeProperty.isNull());
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
            final GfaNode gfaNode = graphVisualizer.getSelectedSegmentProperty().get();
            final Segment segment = gfaNode.getSegments().get(0);
            final int baseOffsetValue = Integer.parseInt(baseString);
            final int radiusValue = Integer.parseInt(radiusString);

            bookmarkStore.addBookmark(new Bookmark(segment.getId(), baseOffsetValue, radiusValue,
                    description.getText()));
            description.clear();
        }

        actionEvent.consume();
    }
}
