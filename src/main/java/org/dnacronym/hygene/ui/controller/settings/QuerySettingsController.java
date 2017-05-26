package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the configuration window.
 */
public final class QuerySettingsController extends AbstractSettingsController {
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
     * When user finished editing the node id {@link TextField}.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    @FXML
    void setNodeId(final KeyEvent keyEvent) {
        getSettings().addRunnable(() -> {
            final TextField source = (TextField) keyEvent.getSource();
            final int newValue = Integer.parseInt(source.getText().replaceAll("[^\\d]", ""));
            getGraphVisualizer().getCenterNodeIdProperty().set(newValue);
        });

        keyEvent.consume();
    }

    /**
     * When user finished editing the range {@link TextField}.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    @FXML
    void setRange(final KeyEvent keyEvent) {
        getSettings().addRunnable(() -> {
            final TextField source = (TextField) keyEvent.getSource();
            final int newValue = Integer.parseInt(source.getText().replaceAll("[^\\d]", ""));
            getGraphVisualizer().getHopsProperty().set(newValue);
        });

        keyEvent.consume();
    }
}
