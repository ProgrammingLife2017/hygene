package org.dnacronym.hygene.ui.genomeindex;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.progressbar.StatusBar;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Stores a single instance of a {@link GenomeIndex} used for navigation of genomes.
 *
 * @see GenomeIndex
 */
public final class GenomeNavigation {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigation.class);

    private final StatusBar statusBar;

    private final BooleanProperty indexFinishedProperty;
    private final ObjectProperty<GenomeIndex> genomeIndexProperty = new SimpleObjectProperty<>();

    private final ObservableList<String> genomeNamesList = FXCollections.observableArrayList();
    private final ReadOnlyListWrapper<String> readOnlyGenomeNames = new ReadOnlyListWrapper<>(genomeNamesList);


    /**
     * Creates an instance of {@link GenomeNavigation}.
     *
     * @param graphStore the {@link GraphStore} whose {@link GfaFile} is observed
     * @param statusBar  the {@link StatusBar} which is used to showAndWait genome indexing progress
     */
    @Inject
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

                        genomeNamesList.setAll(genomeIndex.getGenomeNames());
                    });
                } catch (final SQLException | IOException e) {
                    LOGGER.error("Unable to load genome info from file.", e);
                }
            });

            worker.setDaemon(true); // Automatically shut down this thread when the main thread exits
            worker.start();
        });
    }

    /**
     * Returns the {@link ReadOnlyBooleanProperty} which indicated whether the {@link GenomeIndex} is indexing.
     *
     * @return the {@link ReadOnlyBooleanProperty} which indicated whether the {@link GenomeIndex} is indexing
     */
    public ReadOnlyBooleanProperty getIndexedFinishedProperty() {
        return indexFinishedProperty;
    }

    /**
     * Returns the {@link ReadOnlyObjectProperty} of the {@link GenomeIndex}.
     *
     * @return the {@link ReadOnlyObjectProperty} of the {@link GenomeIndex}
     */
    public ReadOnlyObjectProperty<GenomeIndex> getGenomeIndexProperty() {
        return genomeIndexProperty;
    }

    /**
     * Returns the {@link ReadOnlyListWrapper} of list of current genome names of the current Graph.
     *
     * @return the {@link ReadOnlyListWrapper} of list of current genome names of the current Graph
     */
    public ReadOnlyListWrapper<String> getGenomeNames() {
        return readOnlyGenomeNames;
    }
}
