package org.dnacronym.hygene.ui.genomeindex;

import javafx.collections.FXCollections;
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
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.dialogue.WarningDialogue;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.path.GenomePath;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Controller for the window that allows the user to choose the mapping from GFF genomes to GFA genomes.
 */
public final class GenomeMappingController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeMappingController.class);

    @Inject
    private GraphAnnotation graphAnnotation;
    @Inject
    private GraphStore graphStore;

    @FXML
    private ListView<GenomePath> gfaGenomes;
    @FXML
    private TextField gffGenome;
    @FXML
    private TextField genomeChoice;
    @FXML
    private Button okButton;


    @Override
    @SuppressWarnings("squid:MaximumInheritanceDepth") // Caused by setting the cell factory of gfaGenomes
    public void initialize(final URL location, final ResourceBundle resources) {
        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) ->
                gfaGenomes.setItems(FXCollections.observableArrayList(
                        newValue.getGenomeMapping().entrySet().stream()
                                .map(entry -> new GenomePath(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList()))));

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
        if (genomeChoice.getText().isEmpty()) {
            (new WarningDialogue("Please select a mapping.")).show();
            return;
        }

        try {
            graphAnnotation.setMappedGenome(genomeChoice.getText());
        } catch (final IOException e) {
            LOGGER.error("Unable to build an index for genome " + genomeChoice.getText() + ".", e);
            new ErrorDialogue(e).show();
        }

        final Node source = (Node) actionEvent.getSource();
        source.getScene().getWindow().hide();

        actionEvent.consume();

        LOGGER.info("Genome " + gffGenome.getText() + " from GFF will be mapped onto " + genomeChoice.getText() + ".");
    }
}
