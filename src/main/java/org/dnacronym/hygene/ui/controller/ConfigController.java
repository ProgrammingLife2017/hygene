package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the configuration window.
 */
public final class ConfigController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull Label currentNodeId;
    @FXML
    private @MonotonicNonNull Label currentRange;
    @FXML
    private @MonotonicNonNull TextField nodeId;
    @FXML
    private @MonotonicNonNull TextField range;

    @FXML
    private @MonotonicNonNull Slider nodeHeight;
    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;
    @FXML
    private @MonotonicNonNull CheckBox showBorders;
    @FXML
    private @MonotonicNonNull Slider dashWidth;

    @Override
    @SuppressWarnings("squid:S1067") // Suppress complex if statements for CF
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise Configuration Controller.", e);
            return;
        }

        if (nodeId != null && range != null && currentNodeId != null && currentRange != null && nodeHeight != null
                && edgeColors != null && graphVisualizer != null && showBorders != null && dashWidth != null) {
            nodeId.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
            range.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

            currentNodeId.textProperty().bind(graphVisualizer.getCenterNodeIdProperty().asString());
            currentRange.textProperty().bind(graphVisualizer.getHopsProperty().asString());

            nodeHeight.valueProperty().bindBidirectional(graphVisualizer.getNodeHeightProperty());
            edgeColors.valueProperty().bindBidirectional(graphVisualizer.getEdgeColorProperty());
            showBorders.selectedProperty().bindBidirectional(graphVisualizer.getDisplayBordersProperty());
            dashWidth.valueProperty().bindBidirectional(graphVisualizer.getBorderDashLengthProperty());
        }
    }

    /**
     * Set the node id property in the {@link GraphVisualizer} integer value of the current {@link TextField}.
     * <p>
     * The {@link TextField} should have a {@link TextFormatter} with a {@link NumberStringConverter} so only numbers
     * can be entered in the {@link TextField}. Finally clears the {@link TextField}.
     */
    @FXML
    void setNodeId() {
        if (graphVisualizer != null && nodeId != null) {
            final int newValue = Integer.parseInt(nodeId.getText().replaceAll("[^\\d]", ""));
            graphVisualizer.getCenterNodeIdProperty().set(newValue);
            nodeId.clear();

            LOGGER.info("Center node id set to: " + graphVisualizer.getCenterNodeIdProperty().get());
        }
    }

    /**
     * Set the range property in the {@link GraphVisualizer} integer value of the current {@link TextField}.
     * <p>
     * The {@link TextField} should have a {@link TextFormatter} with a {@link NumberStringConverter} so only numbers
     * can be entered in the {@link TextField}. Finally clears the {@link TextField}.
     */
    @FXML
    void setRange() {
        if (graphVisualizer != null && range != null) {
            final int newValue = Integer.parseInt(range.getText().replaceAll("[^\\d]", ""));
            graphVisualizer.getHopsProperty().set(newValue);
            range.clear();

            LOGGER.info("Range set to: " + graphVisualizer.getHopsProperty().get());
        }
    }

    /**
     * Set the {@link GraphVisualizer}. This allows the sliders to change the properties of the {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller.
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }
}
