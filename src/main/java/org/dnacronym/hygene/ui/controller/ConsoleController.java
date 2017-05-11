package org.dnacronym.hygene.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.util.JFXAppender;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    private static @MonotonicNonNull StringProperty consoleBinding = new SimpleStringProperty();
    @FXML
    private @MonotonicNonNull TextArea console;

    /**
     * Getter for the console binding.
     *
     * @return the console binding.
     */
    @EnsuresNonNull("consoleBinding")
    public static StringProperty getConsoleBinding() {
        if (consoleBinding != null) {
            return consoleBinding;
        }
        consoleBinding = new SimpleStringProperty();
        return consoleBinding;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        JFXAppender.setConsoleBinding(getConsoleBinding());

        Optional.ofNullable(console).orElseThrow(() ->
                new IllegalStateException("Invalid on uninitialized JavaFX FXML element")).setEditable(true);

        getConsoleBinding().addListener((observable, oldValue, newValue) -> {
            if (console != null) {
                console.appendText(newValue);
            }
        });
    }
}
