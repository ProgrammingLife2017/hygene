package org.dnacronym.hygene.ui.genomeindex;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.progressbar.StatusBar;

import java.io.IOException;
import java.sql.SQLException;


public class GenomeNavigation {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigation.class);

    private final StatusBar statusBar;

    private final BooleanProperty indexFinishedProperty;
    private final ObjectProperty<GenomeIndex> genomeIndexProperty = new SimpleObjectProperty<>();
    private final ListProperty<String> genomeNamesProperty = new SimpleListProperty<>();
    private final ReadOnlyListWrapper<String> readOnlyGenomeNames = new ReadOnlyListWrapper<>();

    public GenomeNavigation(final GraphStore graphStore, final StatusBar statusBar) {
        this.statusBar = statusBar;

        indexFinishedProperty = new SimpleBooleanProperty();

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) ->
                triggerGenomeIndex(newValue));
    }


    /**
     * Triggers the population of the genome index.
     *
     * @param gfaFile the new {@link GfaFile} instance
     */
    private void triggerGenomeIndex(final GfaFile gfaFile) {
        indexFinishedProperty.set(false);

        statusBar.monitorTask(progressUpdater -> {
            final Thread worker = new Thread(() -> {
                try {
                    final GenomeIndex genomeIndex = new GenomeIndex(gfaFile, new FileDatabase(gfaFile.getFileName()));
                    genomeIndex.populateIndex(progressUpdater);

                    Platform.runLater(() -> {
                        genomeIndexProperty.set(genomeIndex);
                        indexFinishedProperty.set(true);
                        genomeNamesProperty.setAll(genomeIndex.getGenomeNames());
                    });
                } catch (final SQLException | IOException e) {
                    LOGGER.error("Unable to load genome info from file.", e);
                }
            });

            worker.setDaemon(true); // Automatically shut down this thread when the main thread exits
            worker.start();
        });
    }
}
