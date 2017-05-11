package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.util.JFXAppender;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    @FXML
    private @MonotonicNonNull TextArea console;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Optional.ofNullable(console).orElseThrow(() ->
                new IllegalStateException("Invalid or uninitialized JavaFX FXML element")).setEditable(false);

        JFXAppender.getConsoleBinding().addListener((observable, oldValue, newValue) -> {
            if (console != null) {
                console.appendText(newValue);
            }
        });
    }
}
