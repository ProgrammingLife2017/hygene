package org.dnacronym.hygene.ui.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.FileGraphLocation;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Represents a location in the graph.
 */
public class GraphLocation {
    private static final Logger LOGGER = LogManager.getLogger(GraphLocation.class);

    private final GraphDimensionsCalculator graphDimensionsCalculator;
    private FileGraphLocation fileGraphLocation;

    @Inject
    public GraphLocation(final GraphDimensionsCalculator graphDimensionsCalculator, final GraphStore graphStore) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;

        if (graphStore.getGfaFileProperty().get() != null) {
            loadFileGraphLocation(graphStore.getGfaFileProperty().get());
        }

        graphStore.getGfaFileProperty().addListener((observable, old, newValue) -> loadFileGraphLocation(newValue));
    }

    /**
     * Stores the current position in the database.
     */
    public void store() {
        if (fileGraphLocation == null) {
            return;
        }

        try {
            fileGraphLocation.store(
                    graphDimensionsCalculator.getCenterNodeIdProperty().get(),
                    graphDimensionsCalculator.getRadiusProperty().get()
            );
        } catch (final SQLException e) {
            LOGGER.error("Location could not be persisted", e);
        }
    }

    /**
     * Restores the last position store in the database.
     */
    public void restore() {
        if (fileGraphLocation == null) {
            return;
        }

        try {
            graphDimensionsCalculator.getCenterNodeIdProperty().set(fileGraphLocation.getCenterNodeId());
            graphDimensionsCalculator.getRadiusProperty().set(fileGraphLocation.getRadius());
        } catch (final SQLException e) {
            LOGGER.error("Location could not be restored", e);
        }
    }

    /**
     * Initializes {@link FileGraphLocation}.
     *
     * @param gfaFile the current GFA file
     */
    private void loadFileGraphLocation(final GfaFile gfaFile) {
        try {
            fileGraphLocation = new FileGraphLocation(new FileDatabase(gfaFile.getFileName()));
        } catch (final SQLException | IOException e) {
            LOGGER.error("Unable to load bookmarks from file.", e);
        }
    }
}
