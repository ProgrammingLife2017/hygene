package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the annotation search view.
 */
public final class AnnotationController implements Initializable {
    @Inject
    private AnnotationSearch annotationSearch;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private TextField queryField;
    @FXML
    private TableView<Annotation> resultsTable;
    @FXML
    private TableColumn<Annotation, String> nameColumn;
    @FXML
    private TableColumn<Annotation, String> typeColumn;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        resultsTable.setRowFactory(tableView -> {
            final TableRow<Annotation> annotationTableRow = new TableRow<>();
            annotationTableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    final int nodeId = annotationTableRow.getItem().getStartNodeId();
                    graphDimensionsCalculator.getCenterNodeIdProperty().set(nodeId);
                    graphVisualizer.setSelectedSegment(nodeId);
                }
            });
            return annotationTableRow;
        });

        nameColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getAttributes().get("Name") == null) {
                return new SimpleStringProperty("[unknown]");
            } else {
                return new SimpleStringProperty(cell.getValue().getAttributes().get("Name")[0]);
            }
        });
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));

        resultsTable.itemsProperty().bind(annotationSearch.getSearchResults());
    }

    /**
     * When the user wants to search {@link Annotation}s.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void searchAction(final ActionEvent actionEvent) {
        annotationSearch.search(queryField.getText());

        actionEvent.consume();
    }
}
