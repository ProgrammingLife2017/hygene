package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the basic settings view.
 */
public final class BasicSettingsViewController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(BasicSettingsViewController.class);

    @FXML
    private Slider nodeHeight;
    @FXML
    private ColorPicker edgeColors;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeHeight.setValue(getGraphVisualizer().getNodeHeightProperty().get());
        edgeColors.setValue(getGraphVisualizer().getEdgeColorProperty().get());
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     */
    @FXML
    void nodeHeightSliderDone() {
        getSettings().addRunnable(() -> {
            final double newValue = nodeHeight.getValue();
            getGraphVisualizer().getNodeHeightProperty().setValue(newValue);
        });
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     */
    @FXML
    void edgeColorDone() {
        getSettings().addRunnable(() -> {
            final Color newValue = edgeColors.getValue();
            getGraphVisualizer().getEdgeColorProperty().setValue(newValue);
        });
    }
}
