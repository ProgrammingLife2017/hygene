package org.dnacronym.hygene.ui.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.visualizer.GraphPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    private @MonotonicNonNull GraphPane graphPane;

    @FXML
    private @MonotonicNonNull Slider nodeHeight, nodeWidth;

    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;
    @FXML
    private @MonotonicNonNull Slider edgeWidth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Set the {@link GraphPane}. This allows the sliders to change the properties of the {@link GraphPane}.
     *
     * @param graphPane graph pane to set in the controller.
     */
    void setGraphPane(final GraphPane graphPane) {
        this.graphPane = graphPane;
    }
}
