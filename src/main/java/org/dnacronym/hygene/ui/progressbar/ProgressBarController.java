package org.dnacronym.hygene.ui.progressbar;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * This class creates progress bars and starts tasks that are measured with the progress bar.
 */
public final class ProgressBarController implements Initializable {
    @FXML
    private ProgressBar progressBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // There is nothing to initialize
    }

    /**
     * Binds the progress property and displays the dialog.
     *
     * @param task an executable task to measure progress on
     */
    void activateProgressBar(final Task<?> task) {
        progressBar.progressProperty().bind(task.progressProperty());
    }
}
