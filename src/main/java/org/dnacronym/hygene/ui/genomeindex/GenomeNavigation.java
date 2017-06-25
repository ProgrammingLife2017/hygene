package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.DynamicGenomeIndex;
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
     * Triggers the population of the genome index.
     */
    public void runActionOnIndexedGenome(final String genomeName, final Consumer<DynamicGenomeIndex> action) {
        statusBar.monitorTask(progressUpdater -> {
            final Thread worker = new Thread(() -> {
                try {
                    final DynamicGenomeIndex dynamicGenomeIndex = new DynamicGenomeIndex(
                            graphStore.getGfaFileProperty().get(), genomeName);
                    dynamicGenomeIndex.buildIndex(progressUpdater);
                    action.accept(dynamicGenomeIndex);
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
