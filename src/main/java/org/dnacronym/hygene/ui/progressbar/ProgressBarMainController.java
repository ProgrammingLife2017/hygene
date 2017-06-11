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
public final class ProgressBarMainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ProgressBarMainController.class);

    private ProgressBarMainStatus progressBarMainStatus;

    @FXML
    private ProgressBar progress;
    @FXML
    private Label status;


    /**
     * Creates an instance of {@link ProgressBarMainController}.
     */
    public ProgressBarMainController() {
        try {
            setProgressBarMainStatus(Hygene.getInstance().getProgressBarMainStatus());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        progress.visibleProperty().bind(progressBarMainStatus.getProgressProperty().isNotEqualTo(1)
                .and(progressBarMainStatus.getProgressProperty().isNotEqualTo(0)));
        status.visibleProperty().bind(progressBarMainStatus.getProgressProperty().isNotEqualTo(1)
                .and(progressBarMainStatus.getProgressProperty().isNotEqualTo(0)));

        progress.progressProperty().bind(progressBarMainStatus.getProgressProperty());
        status.textProperty().bind(progressBarMainStatus.getStatusProperty());
    }


    /**
     * Sets the {@link ProgressBarMainStatus} for use by the controller
     *
     * @param progressBarMainStatus the {@link ProgressBarMainStatus} for sue by the controller
     */
    void setProgressBarMainStatus(final ProgressBarMainStatus progressBarMainStatus) {
        this.progressBarMainStatus = progressBarMainStatus;
    }
}
