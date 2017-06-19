package org.dnacronym.hygene.ui.progressbar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the progress bar embedded in the main view of the application.
 */
public final class StatusBarController implements Initializable {
    @Inject
    private StatusBar statusBar;

    @FXML
    private ProgressBar progress;
    @FXML
    private Label status;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        progress.visibleProperty().bind(statusBar.getProgressProperty().isNotEqualTo(1)
                .and(statusBar.getProgressProperty().isNotEqualTo(0)));
        status.visibleProperty().bind(statusBar.getProgressProperty().isNotEqualTo(1)
                .and(statusBar.getProgressProperty().isNotEqualTo(0)));

        progress.progressProperty().bind(statusBar.getProgressProperty());
        status.textProperty().bind(statusBar.getStatusProperty());
    }
}
