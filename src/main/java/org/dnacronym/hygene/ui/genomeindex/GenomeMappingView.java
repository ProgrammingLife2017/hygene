package org.dnacronym.hygene.ui.genomeindex;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private static final String GENOME_MAPPING_VIEW = "/ui/genomeindex/genome_mapping.fxml";

    private final FXMLLoader fxmlLoader;
    private final Stage stage;


    /**
     * Creates an instance of a {@link GenomeMappingView}.
     *
     * @param fxmlLoader the {@link FXMLLoader} used to load the controller
     */
    @Inject
    public GenomeMappingView(final FXMLLoader fxmlLoader) {
        stage = new Stage();
        this.fxmlLoader = fxmlLoader;

        stage.setTitle(TITLE);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(Event::consume); // prevent user from closing stage directly
    }


    /**
     * Show the stage, which blocks the underlying view.
     *
     * @throws UIInitialisationException if the UI has not been initialized
     * @throws IOException               if unable to load the controller
     */
    public void showAndWait() throws UIInitialisationException, IOException {
        stage.initOwner(Hygene.getInstance().getPrimaryStage());

        final URL resource = getClass().getResource(GENOME_MAPPING_VIEW);
        fxmlLoader.setLocation(resource);
        final Parent parent = fxmlLoader.load();
        final Scene rootScene = new Scene(parent);
        stage.setScene(rootScene);

        stage.showAndWait();
    }
}
