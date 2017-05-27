package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Dialogue;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for creating bookmarks.
 */
public final class BookmarkCreateController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BookmarkCreateController.class);

    private GraphVisualizer graphVisualizer;
    private GraphStore graphStore;
    private SimpleBookmarkStore simpleBookmarkStore;

    @FXML
    private AnchorPane bookmarkCreatePane;
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
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);

            final Dialogue dialogue = new Dialogue(Dialogue.DialogueType.ERROR, e);
            dialogue.show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty selectedNodeProperty = graphVisualizer.getSelectedNodeProperty();

        baseOffset.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        radius.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        description.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));
        save.visibleProperty().bind(Bindings.isNotNull(selectedNodeProperty));

        bookmarkCreatePane.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        bookmarkCreatePane.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
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
     * Set the {@link GraphStore} in the controller.
     *
     * @param graphStore {@link GraphStore} to recent in the {@link org.dnacronym.hygene.ui.graph.GraphController}
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
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
     * When the user saves the bookmark.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onSaveAction(final ActionEvent actionEvent) {
        if (graphVisualizer.getSelectedNodeProperty().get() == null) {
            return;
        }

        final String baseString = baseOffset.getText().replaceAll("[^\\d]", "");
        final String radiusString = radius.getText().replaceAll("[^\\d]", "");

        if (!baseString.isEmpty() && !radiusString.isEmpty()) {
            final int nodeId = graphVisualizer.getSelectedNodeProperty().get().getId();
            final int baseOffsetValue = Integer.parseInt(baseString);
            final int radiusValue = Integer.parseInt(radiusString);

            simpleBookmarkStore.addBookmark(new Bookmark(nodeId, baseOffsetValue, radiusValue, description.getText()));

            baseOffset.clear();
            radius.clear();
            description.clear();
        }

        actionEvent.consume();
    }
}
