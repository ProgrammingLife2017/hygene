package org.dnacronym.hygene.ui.settings;

import javafx.event.ActionEvent;
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
 * Settings controller for the basic settings.
 */
public final class BasicSettingsViewController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(BasicSettingsViewController.class);

    @FXML
    private Slider nodeHeight;
    @FXML
    private ColorPicker edgeColor;
    @FXML
    private Slider panningSensitivity;
    @FXML
    private Slider zoomingSensitivity;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeHeight.setValue(getGraphVisualizer().getNodeHeightProperty().get());
        edgeColor.setValue(getGraphVisualizer().getEdgeColorProperty().get());
        panningSensitivity.setValue(getGraphMovementCalculator().getPanningSensitivityProperty().get());
        zoomingSensitivity.setValue(getGraphMovementCalculator().getZoomingSensitivityProperty().get());
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
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void edgeColorDone(final ActionEvent actionEvent) {
        getSettings().addRunnable(() -> {
            final Color newValue = ((ColorPicker) actionEvent.getSource()).getValue();
            getGraphVisualizer().getEdgeColorProperty().setValue(newValue);
            LOGGER.info("Edge color has now been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes sliding the panning sensitivity {@link Slider}.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void panningSensitivitySliderDone(final ActionEvent actionEvent) {
        getSettings().addRunnable(() -> {
            final double newValue = ((Slider) actionEvent.getSource()).getValue();
            getGraphMovementCalculator().getPanningSensitivityProperty().setValue(newValue);
        });
    }

    /**
     * When the user finishes sliding the zooming sensitivity {@link Slider}.
     *
     * @param mouseEvent {@link MouseEvent} associated with this event
     */
    @FXML
    void zoomingSensitivitySliderDone(final MouseEvent mouseEvent) {
        getSettings().addRunnable(() -> {
            final double newValue = ((Slider) mouseEvent.getSource()).getValue();
            getGraphMovementCalculator().getZoomingSensitivityProperty().setValue(newValue);
        });
    }
}
