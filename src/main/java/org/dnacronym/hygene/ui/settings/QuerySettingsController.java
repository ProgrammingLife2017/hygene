package org.dnacronym.hygene.ui.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Settings controller for the current query.
 */
public final class QuerySettingsController implements Initializable {
    @Inject
    private Settings settings;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;

    @FXML
    private Label currentNodeId;
    @FXML
    private Label currentRadius;
    @FXML
    private TextField nodeId;
    @FXML
    private TextField radius;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeId.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        radius.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        currentNodeId.textProperty().bind(graphDimensionsCalculator.getCenterNodeIdProperty().asString());
        currentRadius.textProperty().bind(graphDimensionsCalculator.getRadiusProperty().asString());

        graphDimensionsCalculator.getCenterNodeIdProperty().addListener(
                (observable, oldValue, newValue) -> nodeId.setText(String.valueOf(newValue)));
        graphDimensionsCalculator.getRadiusProperty().addListener(
                (observable, oldValue, newValue) -> radius.setText(String.valueOf(newValue)));
    }

    /**
     * When user finished editing the node id {@link TextField}.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    @FXML
    void setNodeId(final KeyEvent keyEvent) {
        settings.addRunnable(() -> {
            final TextField source = (TextField) keyEvent.getSource();
            final int newValue = Integer.parseInt(source.getText().replaceAll("[^\\d]", ""));
            graphDimensionsCalculator.getViewPointProperty()
                    .set(graphDimensionsCalculator.getGraphProperty().get().getRealStartXPosition(newValue));
        });
    }

    /**
     * When user finished editing the radius {@link TextField}.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    @FXML
    void setRadius(final KeyEvent keyEvent) {
        settings.addRunnable(() -> {
            final TextField source = (TextField) keyEvent.getSource();
            final int newValue = Integer.parseInt(source.getText().replaceAll("[^\\d]", ""));
            graphDimensionsCalculator.getViewRadiusProperty().set(newValue * 2000);
        });
    }
}
