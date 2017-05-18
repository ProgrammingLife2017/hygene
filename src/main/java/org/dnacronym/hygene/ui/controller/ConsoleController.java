package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.util.JFXAppender;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;


    @FXML
    private @MonotonicNonNull TextArea console;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise ConsoleController.", e);
            return;
        }

        Optional.ofNullable(console).orElseThrow(() ->
                new IllegalStateException("Invalid or uninitialized JavaFX FXML element")).setEditable(false);

        JFXAppender.getConsoleBinding().addListener((observable, oldValue, newValue) -> {
            if (console != null) {
                console.appendText(newValue);
            }
        });
    }

    /**
     * Set the {@link GraphVisualizer} in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} to store in the {@link ConsoleController}.
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }
}
