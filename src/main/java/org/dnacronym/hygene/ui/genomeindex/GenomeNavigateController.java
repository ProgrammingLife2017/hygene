package org.dnacronym.hygene.ui.genomeindex;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.dialogue.WarningDialogue;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 * Controller for navigating a genome coordinate system.
 */
public final class GenomeNavigateController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigateController.class);

    private GraphVisualizer graphVisualizer;
    private GraphStore graphStore;
    private GenomeIndex genomeIndex;
    private Hygene hygeneInstance;

    private boolean indexFinished;

    @FXML
    private ComboBox<String> genome;
    @FXML
    private Spinner<Integer> base;


    /**
     * Create instance of {@link GenomeNavigateController}.
     */
    public GenomeNavigateController() {
        try {
            hygeneInstance = Hygene.getInstance();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
            new ErrorDialogue(e).show();
        }

        setGraphVisualizer(hygeneInstance.getGraphVisualizer());
        setGraphStore(hygeneInstance.getGraphStore());

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) ->
                triggerGenomeIndex(newValue));
    }


    /**
     * Triggers the population of the genome index.
     *
     * @param gfaFile the new {@link GfaFile} instance
     */
    private void triggerGenomeIndex(final GfaFile gfaFile) {
        indexFinished = false;

        hygeneInstance.getStatusBar().monitorTask(progressUpdater -> {
            final Thread worker = new Thread(() -> {
                try {
                    genomeIndex = new GenomeIndex(gfaFile, new FileDatabase(gfaFile.getFileName()));
                    genomeIndex.populateIndex(progressUpdater);
                    populateGenomeComboBox();
                    indexFinished = true;
                } catch (final SQLException | IOException e) {
                    LOGGER.error("Unable to load genome info from file.", e);
                }
            });
            worker.setDaemon(true); // Automatically shut down this thread when the main thread exits
            worker.start();
        });
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        base.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        base.getValueFactory().setValue(1);
        // Commit changed values on manual edit of spinner
        base.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                base.increment(0); // won't change value, but will commit edited value
            }
        });

        genome.setOnAction(actionEvent -> base.getValueFactory().setValue(1));
        graphStore.getGfaFileProperty().addListener(gfaFile -> {
            genome.getItems().clear();
            base.getValueFactory().setValue(1);
        });
    }

    /**
     * Populates the {@link ComboBox} of genome names.
     */
    private void populateGenomeComboBox() {
        Platform.runLater(() -> {
            genome.getItems().clear();
            genome.getItems().addAll(genomeIndex.getGenomeNames());
            genome.getSelectionModel().select(0);
        });
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
     * On click of the 'go' button for a genome coordinate query.
     *
     * @param actionEvent the event associated with this action
     */
    @FXML
    public void onGoAction(final ActionEvent actionEvent) {
        if (!indexFinished) {
            new WarningDialogue("Genome indexing process still in progress, unable to navigate.").show();
            return;
        }

        final int selectedBase;

        try {
            selectedBase = base.getValue();
        } catch (final NumberFormatException e) {
            LOGGER.warn("Attempted to enter non-numeric input in base field, aborting.");
            return;
        }

        try {
            final GenomePoint genomePoint = genomeIndex.getGenomePoint(genome.getValue(), selectedBase)
                    .orElseThrow(() ->
                            new SQLException("Genome-base combination could not be found in database."));

            hygeneInstance.getGraphDimensionsCalculator().getCenterNodeIdProperty().set(genomePoint.getNodeId());

            graphVisualizer.setSelectedNode(genomePoint.getNodeId());
            hygeneInstance.getSequenceVisualizer().setOffset(genomePoint.getBaseOffsetInNode());
        } catch (SQLException e) {
            LOGGER.error("Error while looking for genome-base index.", e);
            new WarningDialogue("Genome-base combination could not be found in graph.").show();
        }
    }
}
