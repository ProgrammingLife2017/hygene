package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for keeping track of bookmarks.
 */
public class BookmarksController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    @FXML
    private TableColumn<Bookmark, Integer> nodeIdColumn;
    @FXML
    private TableColumn<Bookmark, String> baseColumn;
    @FXML
    private TableColumn<Bookmark, String> descriptionColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
