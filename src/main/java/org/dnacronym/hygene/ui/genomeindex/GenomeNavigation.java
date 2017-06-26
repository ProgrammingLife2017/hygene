package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.progressbar.StatusBar;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Class responsible for navigation within genome coordinate systems.
 */
public final class GenomeNavigation {
    private static final Logger LOGGER = LogManager.getLogger(GenomeNavigation.class);

    @Inject
    private GraphStore graphStore;
    @Inject
    private StatusBar statusBar;

    private final ObservableList<String> genomeNamesList = FXCollections.observableArrayList();
    private final ReadOnlyListWrapper<String> readOnlyGenomeNames = new ReadOnlyListWrapper<>(genomeNamesList);


    /**
     * Creates an instance of {@link GenomeNavigation}.
     *
     * @param graphStore the injected {@link GraphStore} instance
     */
    @Inject
    public GenomeNavigation(final GraphStore graphStore) {
        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> {
            genomeNamesList.clear();
            genomeNamesList.addAll(newValue.getGenomeMapping().entrySet().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList()));
        });
    }


    /**
     * Indexes the genome with the given name and performs the desired {@code action} as soon as this index is complete.
     *
     * @param genomeName the name of the genome
     * @param action     the action to be undertaken once that genome has been indexed
     */
    public void runActionOnIndexedGenome(final String genomeName, final Consumer<GenomeIndex> action) {
        statusBar.monitorTask(progressUpdater -> {
            final Thread worker = new Thread(() -> {
                try {
                    final GenomeIndex genomeIndex = new GenomeIndex(
                            graphStore.getGfaFileProperty().get(), genomeName);
                    genomeIndex.buildIndex(progressUpdater);
                    action.accept(genomeIndex);
                } catch (final IOException e) {
                    LOGGER.error("Unable to populate genome index.", e);
                }
            });

            worker.setDaemon(true); // Automatically shut down this thread when the main thread exits
            worker.start();
        });
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
