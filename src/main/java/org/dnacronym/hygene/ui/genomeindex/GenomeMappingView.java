package org.dnacronym.hygene.ui.genomeindex;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;


/**
 * Custom view that shows a simple scene where the user can choose the GFF GFA genome mapping.
 */
public final class GenomeMappingView {
    private static final String TITLE = "Mapping";
    private static final String GENOME_MAPPING_VIEW = "/ui/genomeindex/genome_mapping_view.fxml";

    private final Stage stage;


    /**
     * Creates an instance of a {@link GenomeMappingView}.
     *
     * @throws IOException if unable to load the controller
     */
    @Inject
    public GenomeMappingView(final FXMLLoader fxmlLoader) throws UIInitialisationException, IOException {
        stage = new Stage();
        stage.setTitle(TITLE);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(Event::consume); // prevent user from closing stage directly

        final URL resource = getClass().getResource(GENOME_MAPPING_VIEW);
        fxmlLoader.setLocation(resource);
        final Scene rootScene = new Scene(fxmlLoader.load());

        stage.setScene(rootScene);
    }


    /**
     * Show the stage, which blocks the underlying view.
     *
     * @throws UIInitialisationException if the UI has not been initialized
     */
    public void showAndWait() throws UIInitialisationException {
        if (stage.getOwner() == null) {
            stage.initOwner(Hygene.getInstance().getPrimaryStage());
        }

        stage.showAndWait();
    }
}
