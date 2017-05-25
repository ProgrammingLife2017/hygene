package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
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
    private ColorPicker edgeColor;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeHeight.setValue(getGraphVisualizer().getNodeHeightProperty().get());
        edgeColor.setValue(getGraphVisualizer().getEdgeColorProperty().get());
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     *
     * @param mouseEvent the {@link MouseEvent}
     */
    @FXML
    void nodeHeightSliderDone(final MouseEvent mouseEvent) {
        getSettings().addRunnable(() -> {
            final double newValue = ((Slider) mouseEvent.getSource()).getValue();
            getGraphVisualizer().getNodeHeightProperty().setValue(newValue);
            LOGGER.info("Node height has now been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     *
     * @param mouseEvent the {@link MouseEvent}
     */
    @FXML
    void edgeColorDone(final MouseEvent mouseEvent) {
        getSettings().addRunnable(() -> {
            final Color newValue = ((ColorPicker) mouseEvent.getSource()).getValue();
            getGraphVisualizer().getEdgeColorProperty().setValue(newValue);
            LOGGER.info("Edge color has now been set to " + newValue + ".");
        });
    }
}
