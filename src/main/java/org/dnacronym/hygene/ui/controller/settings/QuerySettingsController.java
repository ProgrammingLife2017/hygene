package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the configuration window.
 */
public final class QuerySettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(QuerySettingsController.class);

    private Settings settings;
    private GraphVisualizer graphVisualizer;

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
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
            setSettings(Hygene.getInstance().getSettings());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise Configuration Controller.", e);
            return;
        }

        nodeId.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        range.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        currentNodeId.textProperty().bind(graphVisualizer.getCenterNodeIdProperty().asString());
        currentRange.textProperty().bind(graphVisualizer.getHopsProperty().asString());

        graphVisualizer.getCenterNodeIdProperty().addListener(
                (observable, oldValue, newValue) -> nodeId.setText(String.valueOf(newValue)));
        graphVisualizer.getHopsProperty().addListener(
                (observable, oldValue, newValue) -> range.setText(String.valueOf(newValue)));
    }

    /**
     * Sets the {@link Settings} for use by the controller.
     *
     * @param settings {@link Settings} for use by the controller
     */
    void setSettings(final Settings settings) {
        this.settings = settings;
    }

    /**
     * Sets the {@link GraphVisualizer}. This allows the sliders to change the properties of the
     * {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }

    /**
     * Sets the node id property in the {@link GraphVisualizer} integer value of the current {@link TextField}.
     * <p>
     * The {@link TextField} should have a {@link TextFormatter} with a {@link NumberStringConverter} so only numbers
     * can be entered in the {@link TextField}. Finally clears the {@link TextField}.
     */
    @FXML
    void setNodeId() {
        settings.addRunnable(() -> {
            final int newValue = Integer.parseInt(nodeId.getText().replaceAll("[^\\d]", ""));
            graphVisualizer.getCenterNodeIdProperty().set(newValue);

            LOGGER.info("Center node id set to: " + graphVisualizer.getCenterNodeIdProperty().get());
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
        settings.addRunnable(() -> {
            final int newValue = Integer.parseInt(range.getText().replaceAll("[^\\d]", ""));
            graphVisualizer.getHopsProperty().set(newValue);

            LOGGER.info("Range set to: " + graphVisualizer.getHopsProperty().get());
        });
    }
}
