package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.StringTokenizer;


/**
 * Persistent storage for graph dumps.
 */
public final class GraphLoader {
    private static final Logger LOGGER = LogManager.getLogger(GraphLoader.class);

    private static final String TABLE_NAME = "graph_dump";
    private static final String KEY_COLUMN_NAME = "id";
    private static final String KEY_COLUMN_VALUE = "0";
    private static final String NODE_COUNT_COLUMN_NAME = "node_count";
    private static final String DUMP_COLUMN_NAME = "dump";
    private static final String NODE_VALUE_SEPARATOR = " ";
    private static final String NODE_ARRAY_SEPARATOR = "\n";

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

        final StringBuilder graphDump = new StringBuilder();
        for (final int[] node : graph) {
            for (final int value : node) {
                graphDump.append(value).append(NODE_VALUE_SEPARATOR);
            }

            graphDump.deleteCharAt(graphDump.length() - 1);
            graphDump.append(NODE_ARRAY_SEPARATOR);
        }

        try {
            fileDatabaseDriver.insertRow(TABLE_NAME,
                    Arrays.asList(KEY_COLUMN_VALUE, Integer.toString(graph.length), graphDump.toString()));
        } catch (final SQLException e) {
            throw new UnexpectedDatabaseException("Failed to dump graph into database.", e);
        }
    }

    /**
     * Restores the graph dump into a graph.
     *
     * @return a graph
     */
    public int[][] restoreGraph() {
        LOGGER.info("Restoring graph from storage.");

        if (!hasGraph()) {
            throw new IllegalStateException("Fuck you mate.");
        }

        final int nodeCount;
        final String graphDump;

        try {
            nodeCount = Integer.parseInt(fileDatabaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME,
                    KEY_COLUMN_VALUE, NODE_COUNT_COLUMN_NAME));
            graphDump = fileDatabaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME, KEY_COLUMN_VALUE,
                    DUMP_COLUMN_NAME);
        } catch (final SQLException e) {
            throw new UnexpectedDatabaseException("Failed to retrieve graph dump.", e);
        }

        final int[][] graph = new int[nodeCount][];
        final StringTokenizer dumpTokenizer = new StringTokenizer(graphDump, NODE_ARRAY_SEPARATOR);

        String node;
        StringTokenizer nodeTokenizer;
        int nodeIndex = 0;
        while (dumpTokenizer.hasMoreTokens()) {
            node = dumpTokenizer.nextToken();
            nodeTokenizer = new StringTokenizer(node, NODE_VALUE_SEPARATOR);

            graph[nodeIndex] = new int[nodeTokenizer.countTokens()];

            int valueIndex = 0;
            while (nodeTokenizer.hasMoreTokens()) {
                graph[nodeIndex][valueIndex] = Integer.parseInt(nodeTokenizer.nextToken());
                valueIndex++;
            }

            nodeIndex++;
        }

        return graph;
    }
}
