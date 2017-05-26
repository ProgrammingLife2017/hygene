package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;


/**
 * Class responsible for storing and retrieving genome coordinate system index points.
 */
public final class FileGenomeIndex {
    static final String TABLE_NAME = "genome_index";

    private static final Logger LOGGER = LogManager.getLogger(GraphLoader.class);

    private static final String GENOME_ID_COLUMN_NAME = "genome_id";
    private static final String BASE_COLUMN_NAME = "base";
    private static final String NODE_ID_COLUMN_NAME = "node_id";

    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a {@link FileGenomeIndex} instance.
     *
     * @param fileDatabase the database to contain that genome index
     */
    public FileGenomeIndex(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding genome index points.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding genome index points
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(GENOME_ID_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(BASE_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(NODE_ID_COLUMN_NAME, ColumnType.INTEGER);

        return globalTable;
    }


    /**
     * Adds a new genome index point with the given data attributes.
     *
     * @param genomeId a numeric ID representing the genome this index belongs to
     * @param base     the base count at this index point
     * @param nodeId   the ID of the node that belongs to that index point
     * @throws SQLException in the case of an error during SQL operations
     */
    public void addGenomeIndexPoint(final int genomeId, final int base, final int nodeId) throws SQLException {
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(
                String.valueOf(genomeId),
                String.valueOf(base),
                String.valueOf(nodeId)
        ));
    }

    /**
     * Retrieves the ID of the node that is closest to the given base in the given genome.
     *
     * @param genomeId the ID of the genome
     * @param base     the base number
     * @return the ID of the closest node
     * @throws SQLException in the case of an error during SQL operations
     */
    public int getClosestNodeToBase(final int genomeId, final int base) throws SQLException {
        final String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + GENOME_ID_COLUMN_NAME + "=" + genomeId
                + " ORDER BY ABS(" + base + " - " + BASE_COLUMN_NAME + ")"
                + " LIMIT 1";

        return (Integer) fileDatabaseDriver.executeCustomSingleRowQuery(sql, resultSet -> {
            try {
                return resultSet.getInt(NODE_ID_COLUMN_NAME);
            } catch (final SQLException e) {
                LOGGER.error("Unable to retrieve closest node to base " + base + " in genome no. " + genomeId + ".", e);
                return null;
            }
        });
    }
}
