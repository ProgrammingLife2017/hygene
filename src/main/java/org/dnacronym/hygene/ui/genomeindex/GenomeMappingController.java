package org.dnacronym.hygene.ui.genomeindex;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.dialogue.WarningDialogue;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.path.GenomePath;

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
    private ListView<GenomePath> gfaGenomes;
    @FXML
    private TextField gffGenome;
    @FXML
    private TextField genomeChoice;
    @FXML
    private Button okButton;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        gfaGenomes.setItems(graphVisualizer.getGenomePathsProperty());
        gfaGenomes.setCellFactory(listView -> {
            final ListCell<GenomePath> genomePathCell = new ListCell<GenomePath>() {
                @Override
                protected void updateItem(final GenomePath item, final boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
            genomePathCell.setOnMousePressed(event -> {
                if (!genomePathCell.isEmpty()) {
                    genomeChoice.setText(genomePathCell.itemProperty().get().getName());
                }
                event.consume();
            });

            return genomePathCell;
        });

        gffGenome.textProperty().bind(graphAnnotation.getAnnotationsSequenceId());

        okButton.disableProperty().bind(genomeChoice.textProperty().isEmpty());
    }

    /**
     * The action to fire when the user clicks the "Ok" button.
     * <p>
     * Sets the mapped genome in {@link GraphAnnotation} to the value in the genome choice textfield.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void okAction(final ActionEvent actionEvent) {
        graphAnnotation.setMappedGenome(genomeChoice.getText());
        if (genomeChoice.getText().isEmpty()) {
            (new WarningDialogue("Please select a mapping.")).show();
            return;
        }

        final Node source = (Node) actionEvent.getSource();
        source.getScene().getWindow().hide();

        actionEvent.consume();

        LOGGER.info("Genome " + gffGenome.getText() + " from GFF will be mapped onto " + genomeChoice.getText() + ".");
    }
}
