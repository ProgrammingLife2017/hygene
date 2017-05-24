package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the configuration window.
 */
public final class QuerySettingsController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(QuerySettingsController.class);

    @FXML
    private Label currentNodeId;
    @FXML
    private Label currentRange;
    @FXML
    private TextField nodeId;
    @FXML
    private TextField range;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeId.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        range.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        currentNodeId.textProperty().bind(getGraphVisualizer().getCenterNodeIdProperty().asString());
        currentRange.textProperty().bind(getGraphVisualizer().getHopsProperty().asString());

        getGraphVisualizer().getCenterNodeIdProperty().addListener(
                (observable, oldValue, newValue) -> nodeId.setText(String.valueOf(newValue)));
        getGraphVisualizer().getHopsProperty().addListener(
                (observable, oldValue, newValue) -> range.setText(String.valueOf(newValue)));
    }

    /**
     * Sets the node id property in the {@link GraphVisualizer} integer value of the current {@link TextField}.
     * <p>
     * The {@link TextField} should have a {@link TextFormatter} with a {@link NumberStringConverter} so only numbers
     * can be entered in the {@link TextField}. Finally clears the {@link TextField}.
     */
    @FXML
    void setNodeId() {
        getSettings().addRunnable(() -> {
            final int newValue = Integer.parseInt(nodeId.getText().replaceAll("[^\\d]", ""));
            getGraphVisualizer().getCenterNodeIdProperty().set(newValue);

            LOGGER.info("Center node id set to: " + getGraphVisualizer().getCenterNodeIdProperty().get());
        });
    }

    /**
     * Sets the range property in the {@link GraphVisualizer} integer value of the current {@link TextField}.
     * <p>
     * The {@link TextField} should have a {@link TextFormatter} with a {@link NumberStringConverter} so only numbers
     * can be entered in the {@link TextField}. Finally clears the {@link TextField}.
     */
    @FXML
    void setRange() {
        getSettings().addRunnable(() -> {
            final int newValue = Integer.parseInt(range.getText().replaceAll("[^\\d]", ""));
            getGraphVisualizer().getHopsProperty().set(newValue);

            LOGGER.info("Range set to: " + getGraphVisualizer().getHopsProperty().get());
        });
    }
}
