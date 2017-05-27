package org.dnacronym.hygene.ui.node;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 *
 */
public class SequenceController implements Initializable {
    private static final int TEXT_FIELD_WIDTH = 20;
    private static final int TEXT_FIELD_HEIGHT = 40;

    @FXML
    private TextField lengthField;
    @FXML
    private GridPane sequenceGrid;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nothing to initialize
    }

    /**
     * @param sequence
     * @param maxWindowWidth
     */
    void setSequence(final String sequence, final double maxWindowWidth) {
        lengthField.setText(String.valueOf(sequence.length()));

        int column = 0;
        for (int i = 0; i < sequence.length(); i++) {
            final Label baseField = new Label();
            final Label positionField = new Label();
            baseField.setText(String.valueOf(sequence.charAt(i)));
            positionField.setText(String.valueOf(i));

            baseField.setPrefWidth(TEXT_FIELD_WIDTH);
            baseField.setPrefHeight(TEXT_FIELD_HEIGHT);
            positionField.setPrefWidth(TEXT_FIELD_WIDTH);
            positionField.setPrefHeight(TEXT_FIELD_HEIGHT);

            sequenceGrid.addColumn(column, baseField);
            sequenceGrid.addColumn(column, positionField);

            column++;
            if (column * TEXT_FIELD_WIDTH > maxWindowWidth) {
                column = 0;
            }
        }
    }
}
