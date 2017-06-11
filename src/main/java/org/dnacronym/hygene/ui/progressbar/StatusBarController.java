package org.dnacronym.hygene.ui.progressbar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the progress bar embedded in the main view of the application.
 */
public final class StatusBarController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(StatusBarController.class);

    private StatusBar statusBar;

    @FXML
    private ProgressBar progress;
    @FXML
    private Label status;


    /**
     * Creates an instance of {@link StatusBarController}.
     */
    public StatusBarController() {
        try {
            setStatusBar(Hygene.getInstance().getStatusBar());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        progress.visibleProperty().bind(statusBar.getProgressProperty().isNotEqualTo(1)
                .and(statusBar.getProgressProperty().isNotEqualTo(0)));
        status.visibleProperty().bind(statusBar.getProgressProperty().isNotEqualTo(1)
                .and(statusBar.getProgressProperty().isNotEqualTo(0)));

        progress.progressProperty().bind(statusBar.getProgressProperty());
        status.textProperty().bind(statusBar.getStatusProperty());
    }


    /**
     * Sets the {@link StatusBar} for use by the controller.
     *
     * @param statusBar the {@link StatusBar} for use by the controller
     */
    void setStatusBar(final StatusBar statusBar) {
        this.statusBar = statusBar;
    }
}
