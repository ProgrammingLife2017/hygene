package org.dnacronym.hygene.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.parser.ProgressUpdater;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Persistent storage for graph dumps.
 */
@SuppressFBWarnings(
        value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Neither relevant nor practical for a local, isolated file database"
)
public final class GraphLoader {
    private static final Logger LOGGER = LogManager.getLogger(GraphLoader.class);

    private static final String TABLE_NAME = "graph_dump";
    private static final String KEY_COLUMN_NAME = "id";
    private static final String KEY_COLUMN_VALUE = "0";
    private static final String NODE_COUNT_COLUMN_NAME = "node_count";
    private static final String DUMP_COLUMN_NAME = "dump";

    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a new {@link GraphLoader}.
     *
     * @param fileDatabase the {@link FileDatabase} to dump the graph to
     */
    public GraphLoader(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();

        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable(TABLE_NAME);
        fileDatabaseTable.addColumn(KEY_COLUMN_NAME, ColumnType.TEXT);
        fileDatabaseTable.addColumn(NODE_COUNT_COLUMN_NAME, ColumnType.TEXT);
        fileDatabaseTable.addColumn(DUMP_COLUMN_NAME, ColumnType.BLOB);

        try {
            fileDatabaseDriver.setUpTable(fileDatabaseTable);
        } catch (final SQLException e) {
            throw new UnexpectedDatabaseException("Failed to set up graph dump table.", e);
        }
    }


    /**
     * Returns {@code true} iff. a graph dump has been stored.
     *
     * @return {@code true} iff. a graph dump has been stored
     */
    public boolean hasGraph() {
        try {
            return fileDatabaseDriver.hasRow(TABLE_NAME, KEY_COLUMN_NAME, KEY_COLUMN_VALUE);
        } catch (final SQLException e) {
            LOGGER.error("Failed to check for graph dump existence.", e);
            return false;
        }
    }

    /**
     * Deletes the graph dump, or does nothing if there is none.
     */
    public void deleteGraph() {
        try {
            fileDatabaseDriver.deleteRow(TABLE_NAME, KEY_COLUMN_NAME, KEY_COLUMN_VALUE);
        } catch (final SQLException e) {
            throw new UnexpectedDatabaseException("Failed to delete graph dump.", e);
        }
    }

    /**
     * Dumps the given graph into the file database.
     *
     * @param graph the graph to dump
     */
    public void dumpGraph(final int[][] graph) {
        LOGGER.info("Dumping graph to storage.");

        if (hasGraph()) {
            deleteGraph();
        }

        try {
            LOGGER.info("Write internal data structure to temporary file.");
            final GraphArrayFile cache = new GraphArrayFile(Files.getInstance().getTemporaryFile("hygene-cache"));
            cache.write(graph);

            LOGGER.info("Load temporary file into the database.");
            fileDatabaseDriver.enableFileIO();
            fileDatabaseDriver.raw("INSERT INTO " + TABLE_NAME + " VALUES(" + KEY_COLUMN_VALUE + ","
                    + graph.length + ",readfile('" + cache.getAbsolutePath() + "'))");
        } catch (SQLException | IOException e) {
            throw new UnexpectedDatabaseException("Failed to dump graph into database.", e);
        }

        LOGGER.info("Finished dumping graph to storage.");
    }

    /**
     * Restores the graph dump into a graph.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a graph
     * @throws IOException if we cannot read from the cache file
     */
    public int[][] restoreGraph(final ProgressUpdater progressUpdater) throws IOException {
        LOGGER.info("Restoring graph from storage.");

        if (!hasGraph()) {
            throw new IllegalStateException("There is no graph present in the database to be restored.");
        }

        final GraphArrayFile cache = new GraphArrayFile(Files.getInstance().getTemporaryFile("hygene-cache"));

        try {
            final int nodeCount = Integer.parseInt(fileDatabaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME,
                    KEY_COLUMN_VALUE, NODE_COUNT_COLUMN_NAME));

            LOGGER.info("Creating temporary file with internal data structure representation.");
            fileDatabaseDriver.enableFileIO();
            fileDatabaseDriver.raw(
                    "SELECT writefile('" + cache.getAbsolutePath() + "', dump) FROM " + TABLE_NAME + " LIMIT 1");

            LOGGER.info("Load temporary file into memory and parse to internal data structure.");
            return cache.read(nodeCount, progressUpdater);
        } catch (final SQLException e) {
            throw new UnexpectedDatabaseException("Failed to retrieve graph dump.", e);
        }
    }
}
