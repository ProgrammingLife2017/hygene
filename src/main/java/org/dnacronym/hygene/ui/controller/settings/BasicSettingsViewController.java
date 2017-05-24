package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public final class BasicSettingsViewController extends AbstractSettingsController implements Initializable {
    protected static final Logger LOGGER = LogManager.getLogger(BasicSettingsViewController.class);

    @FXML
    private Slider nodeHeight;
    @FXML
    private ColorPicker edgeColors;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        nodeHeight.setValue(graphVisualizer.getNodeHeightProperty().get());
        edgeColors.setValue(graphVisualizer.getEdgeColorProperty().get());
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     */
    @FXML
    void nodeHeightSliderDone() {
        settings.addRunnable(() -> {
            final double newValue = nodeHeight.getValue();
            graphVisualizer.getNodeHeightProperty().setValue(newValue);
        });
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     */
    @FXML
    void edgeColorDone() {
        settings.addRunnable(() -> {
            final Color newValue = edgeColors.getValue();
            graphVisualizer.getEdgeColorProperty().setValue(newValue);
        });
    }
}
