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
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for navigating a genome coordinate system.
 */
public final class GenomeNavigateController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigateController.class);

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GenomeNavigation genomeNavigation;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private SequenceVisualizer sequenceVisualizer;

    @FXML
    private ComboBox<String> genome;
    @FXML
    private Spinner<Integer> base;


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
     * On click of the 'go' button for a genome coordinate query.
     *
     * @param actionEvent the event associated with this action
     */
    @FXML
    public void onGoAction(final ActionEvent actionEvent) {
        final int selectedBase;

        try {
            selectedBase = base.getValue();
        } catch (final NumberFormatException e) {
            LOGGER.warn("Attempted to enter non-numeric input in base field, aborting.");
            return;
        }

        genomeNavigation.runActionOnIndexedGenome(genome.getValue(), dynamicGenomeIndex -> Platform.runLater(() -> {
            final int nodeId = dynamicGenomeIndex.getNodeByBase(selectedBase);
            graphDimensionsCalculator.getCenterNodeIdProperty().set(nodeId);
            graphVisualizer.setSelectedSegment(nodeId);

            sequenceVisualizer.setOffset(dynamicGenomeIndex.getBaseOffsetWithinNode(selectedBase));
        }));
    }
}
