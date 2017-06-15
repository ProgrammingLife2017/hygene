package org.dnacronym.hygene.ui.genomeindex;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.dialogue.WarningDialogue;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 * Controller for navigating a genome coordinate system.
 */
public final class GenomeNavigateController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigateController.class);

    private GraphVisualizer graphVisualizer;
    private final GenomeNavigation genomeNavigation;
    private Hygene hygeneInstance;

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
        genomeNavigation = hygeneInstance.getGenomeNavigation();
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

        genome.itemsProperty().bind(genomeNavigation.getGenomeNames());
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
     * On click of the 'go' button for a genome coordinate query.
     *
     * @param actionEvent the event associated with this action
     */
    @FXML
    public void onGoAction(final ActionEvent actionEvent) {
        if (!genomeNavigation.getIndexedFinishedProperty().get()) {
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
            final GenomePoint genomePoint = genomeNavigation.getGenomeIndexProperty().get()
                    .getGenomePoint(genome.getValue(), selectedBase)
                    .orElseThrow(() ->
                            new SQLException("Genome-base combination could not be found in database."));

            hygeneInstance.getGraphDimensionsCalculator().getCenterNodeIdProperty().set(genomePoint.getNodeId());

            graphVisualizer.setSelectedSegment(genomePoint.getNodeId());
            hygeneInstance.getSequenceVisualizer().setOffset(genomePoint.getBaseOffsetInNode());
        } catch (SQLException e) {
            LOGGER.error("Error while looking for genome-base index.", e);
            new WarningDialogue("Genome-base combination could not be found in graph.").show();
        }
    }
}
