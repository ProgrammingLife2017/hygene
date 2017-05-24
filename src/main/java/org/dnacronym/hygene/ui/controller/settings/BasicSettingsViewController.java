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
    private ColorPicker edgeColor;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeHeight.setValue(getGraphVisualizer().getNodeHeightProperty().get());
        edgeColor.setValue(getGraphVisualizer().getEdgeColorProperty().get());
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     */
    @FXML
    void nodeHeightSliderDone() {
        getSettings().addRunnable(() -> {
            final double newValue = nodeHeight.getValue();
            getGraphVisualizer().getNodeHeightProperty().setValue(newValue);
            LOGGER.info("Node height has now been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     */
    @FXML
    void edgeColorDone() {
        getSettings().addRunnable(() -> {
            final Color newValue = edgeColor.getValue();
            getGraphVisualizer().getEdgeColorProperty().setValue(newValue);
            LOGGER.info("Edge color has now been set to " + newValue + ".");
        });
    }

    /**
     * Gets node height.
     *
     * @return the node height
     */
    Slider getNodeHeight() {
        return nodeHeight;
    }

    /**
     * Sets node height.
     *
     * @param nodeHeight the node height
     */
    void setNodeHeight(final Slider nodeHeight) {
        this.nodeHeight = nodeHeight;
    }

    /**
     * Gets edge colors.
     *
     * @return the edge colors
     */
    ColorPicker getEdgeColor() {
        return edgeColor;
    }

    /**
     * Sets edge colors.
     *
     * @param edgeColor the edge colors
     */
    void setEdgeColor(final ColorPicker edgeColor) {
        this.edgeColor = edgeColor;
    }
}
