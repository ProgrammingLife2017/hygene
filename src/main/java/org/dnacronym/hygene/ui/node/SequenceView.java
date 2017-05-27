package org.dnacronym.hygene.ui.node;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.core.Files;

import java.io.IOException;
import java.net.URL;

public final class SequenceView {
    private static final String SEQUENCE_VIEW = "/ui/node/sequence_view.fxml";
    private static final int SEQUENCE_WINDOW_WIDTH = 800;

    private Stage stage;
    private SequenceController sequenceController;


    public SequenceView() throws IOException {
        final Stage stage = new Stage();

        final URL resource = Files.getInstance().getResourceUrl(SEQUENCE_VIEW);
        final FXMLLoader fxmlLoader = new FXMLLoader(resource);
        final Parent parent = fxmlLoader.load();
        sequenceController = fxmlLoader.getController();

        final Scene scene = new Scene(parent);
        stage.setScene(scene);

        setStage(stage);
    }


    void setStage(final Stage stage) {
        this.stage = stage;
    }

    void show(final String sequence) {
        sequenceController.setSequence(sequence, SEQUENCE_WINDOW_WIDTH);
        stage.show();
    }
}
