package org.dnacronym.hygene.ui.menu;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the view menu.
 */
public final class ViewMenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ViewMenuController.class);

    private SimpleBookmarkStore simpleBookmarkStore;
    private SequenceVisualizer sequenceVisualizer;
    private GraphVisualizer graphVisualizer;
    private GraphStore graphStore;

    @FXML
    private MenuItem toggleBookmarkTable;
    @FXML
    private MenuItem toggleBookmarkCreate;
    @FXML
    private MenuItem toggleNodeProperties;
    @FXML
    private MenuItem toggleSequenceVisualizer;


    /**
     * Creates an instance of {@link ViewMenuController}.
     */
    public ViewMenuController() {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setSequenceVisualizer(Hygene.getInstance().getSequenceVisualizer());
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        toggleBookmarkTable.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        toggleBookmarkCreate.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        toggleNodeProperties.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        toggleSequenceVisualizer.disableProperty().bind(graphStore.getGfaFileProperty().isNull());

        toggleBookmarkTable.textProperty().bind(Bindings.when(simpleBookmarkStore.getTableVisibleProperty())
                .then("Hide _bookmarks table")
                .otherwise("Show _bookmarks table"));
        toggleBookmarkCreate.textProperty().bind(Bindings.when(simpleBookmarkStore.getBookmarkCreateVisibleProperty())
                .then("Hide bookmar_k create")
                .otherwise("Show bookmar_k create"));
        toggleNodeProperties.textProperty().bind(Bindings.when(graphVisualizer.getNodePropertiesVisibleProperty())
                .then("Hide node _properties")
                .otherwise("Show node _properties"));
        toggleSequenceVisualizer.textProperty().bind(Bindings.when(sequenceVisualizer.getVisibleProperty())
                .then("Hide s_equence view")
                .otherwise("Show s_equence view"));
    }

    /**
     * Sets the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore the {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
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
     * Set the {@link GraphVisualizer}, whose selected node can be bound to the UI elements in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} who's selected node we are interested in
     * @see GraphVisualizer#selectedNodeProperty
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Sets the {@link GraphStore} for use by the controller.
     *
     * @param graphStore the {@link GraphStore} for use by the controller
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * When the user wants to toggle the visibility of the BookmarksTable.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleBookmarkTableAction(final ActionEvent actionEvent) {
        simpleBookmarkStore.getTableVisibleProperty().set(!simpleBookmarkStore.getTableVisibleProperty().get());
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the node properties pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleNodePropertiesAction(final ActionEvent actionEvent) {
        graphVisualizer.getNodePropertiesVisibleProperty().set(
                !graphVisualizer.getNodePropertiesVisibleProperty().get()
        );
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the bookmark create pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleBookmarkCreateAction(final ActionEvent actionEvent) {
        simpleBookmarkStore.getBookmarkCreateVisibleProperty().set(
                !simpleBookmarkStore.getBookmarkCreateVisibleProperty().get()
        );
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the sequence visualizer pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleSequenceVisualizerAction(final ActionEvent actionEvent) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        actionEvent.consume();
    }
}
