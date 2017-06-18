package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


/**
 * Persistent storage for graph dumps.
 */
final class GraphLoader {
    private static final Logger LOGGER = LogManager.getLogger(GraphLoader.class);
    private static final String EXTENSION = ".hygenecache";

    private final File cacheFile;


    /**
     * Constructs and initializes {@link GraphLoader}.
     *
     * @param fileName the filename of the related GFA file
     */
    GraphLoader(final String fileName) {
        this.cacheFile = new File(fileName + EXTENSION);
    }


    /**
     * Returns {@code true} iff. a graph dump has been stored.
     *
     * @return {@code true} iff. a graph dump has been stored
     */
    boolean hasGraph() {
        return cacheFile.exists();
    }

    /**
     * Dumps the given graph into the specified file.
     *
     * @param graph    the graph to dump
     */
    void dumpGraph(final int[][] graph) {
        LOGGER.info("Dumping graph to storage.");

        try {
            LOGGER.info("Write internal data structure to temporary file.");
            final GraphArrayFile cache = new GraphArrayFile(cacheFile);
            cache.write(graph);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to dump graph into database.", e);
        }

        LOGGER.info("Finished dumping graph to storage.");
    }

    /**
     * Restores the graph dump in the specified file into a graph.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a graph
     * @throws IOException if we cannot read from the cache file
     */
    int[][] restoreGraph(final ProgressUpdater progressUpdater) throws IOException {
        LOGGER.info("Restoring graph from storage.");

        if (!hasGraph()) {
            throw new IllegalStateException("There is no graph present in the database to be restored.");
        }

        final GraphArrayFile cache = new GraphArrayFile(cacheFile);

        LOGGER.info("Load temporary file into memory and parse to internal data structure.");
        return cache.read(progressUpdater);
    }
}
