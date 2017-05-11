package org.dnacronym.hygene.ui.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    @FXML
    private @MonotonicNonNull Slider nodeHeight, nodeWidth;

    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
