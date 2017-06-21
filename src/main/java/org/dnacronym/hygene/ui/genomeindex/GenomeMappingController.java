package org.dnacronym.hygene.ui.genomeindex;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the window that allows the user to choose the mapping from GFF genomes to GFA genomes.
 */
public final class GenomeMappingController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeMappingController.class);

    @Inject
    private GraphAnnotation graphAnnotation;
    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private ListView gfaGenomes;
    @FXML
    private TextField gffGenome;
    @FXML
    private TextField genomeChoice;
    @FXML
    private Button okButton;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        okButton.disableProperty().bind(genomeChoice.textProperty().isEmpty());
    }

    @FXML
    void okAction(final ActionEvent actionEvent) {
        actionEvent.consume();
    }
}
