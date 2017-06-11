package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.parser.ParseException;
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

    @FXML
    private AnchorPane genomeNavigatePane;
    @FXML
    private ComboBox<String> genome;
    @FXML
    private Spinner<Integer> base;


    /**
     * Create instance of {@link GenomeNavigateController}.
     */
    public GenomeNavigateController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setGraphStore(Hygene.getInstance().getGraphStore());

            graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    genomeIndex = new GenomeIndex(newValue, new FileDatabase(newValue.getFileName()));
                    genomeIndex.populateIndex();
                    populateGenomeComboBox();
                } catch (final SQLException | IOException | ParseException e) {
                    LOGGER.error("Unable to load genome info from file.", e);
                }
            });
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        base.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        base.getValueFactory().setValue(1);

        genomeNavigatePane.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        genomeNavigatePane.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
    }

    private void populateGenomeComboBox() {
        genome.getItems().clear();
        genome.getItems().addAll(genomeIndex.getGenomeNames());
        genome.getSelectionModel().select(0);
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

    @FXML
    public void onGoAction(final ActionEvent actionEvent) {
        try {
            final GenomePoint genomePoint = genomeIndex.getClosestNode(genome.getValue(), base.getValue()).get();

            if (genomePoint.getNodeId() == -1) {
                new WarningDialogue("Genome-base combination could not be found in graph.").show();
                return;
            }
            graphVisualizer.getSelectedNodeProperty().setValue(
                    graphStore.getGfaFileProperty().get().getGraph().getNode(genomePoint.getNodeId()));
        } catch (SQLException e) {
            LOGGER.error("Error while looking for genome-base index.", e);
        }

        actionEvent.consume();
    }
}
