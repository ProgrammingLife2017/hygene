package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;

import java.sql.SQLException;
import java.util.Arrays;


/**
 * Class responsible for storing and retrieving genome coordinate system index points.
 */
public final class FileGenomeIndex {
    private static final Logger LOGGER = LogManager.getLogger(GraphLoader.class);

    static final String TABLE_NAME = "genome_index";

    private static final String GENOME_ID_COLUMN_NAME = "genome_id";
    private static final String BASE_COLUMN_NAME = "base";
    private static final String NODE_ID_COLUMN_NAME = "node_id";

    private final FileDatabaseDriver fileDatabaseDriver;
    private final FileDatabase fileDatabase;


    /**
     * Constructs a {@link FileGenomeIndex} instance.
     *
     * @param fileDatabase the database to contain that genome index
     */
    public FileGenomeIndex(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
        this.fileDatabase = fileDatabase;
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
     * Returns {@code true} iff. the genomes in the file have been indexed before.
     * <p>
     * For the time being, this check consists of a simple query for the number of rows in the index table.
     *
     * @return {@code true} iff. the genomes in the file have been indexed before
     * @throws SQLException in the case of an error during SQL operations
     */
    public boolean isIndexed() throws SQLException {
        return fileDatabase.getFileMetadata().isIndexed();
    }

    /**
     * Permanently deletes all entries of the genome index.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    public void cleanIndex() throws SQLException {
        fileDatabaseDriver.deleteAllFromTable(TABLE_NAME);
        fileDatabase.getFileMetadata().setIndexedState(false);
    }

    /**
     * Marks the index as being complete.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    public void markIndexAsComplete() throws SQLException {
        fileDatabase.getFileMetadata().setIndexedState(true);
    }

    /**
     * Adds a new genome index point with the given data attributes.
     *
     * @param genomePoint the point to be indexed ({@link GenomePoint#baseOffsetInNode} will be discarded)
     * @throws SQLException in the case of an error during SQL operations
     */
    public void addGenomeIndexPoint(final GenomePoint genomePoint) throws SQLException {
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(
                String.valueOf(genomePoint.getGenomeId()),
                String.valueOf(genomePoint.getBase()),
                String.valueOf(genomePoint.getNodeId())
        ));
    }

    /**
     * Retrieves the ID of the node that contains the given base in the given genome.
     *
     * @param genomeId the ID of the genome
     * @param base     the base number
     * @return the ID of the node containing that base, or -1 if it could not be found
     * @throws SQLException in the case of an error during SQL operations
     */
    public @Nullable GenomePoint getGenomePoint(final int genomeId, final int base) throws SQLException {
        final String sql = "SELECT " + BASE_COLUMN_NAME + "," + NODE_ID_COLUMN_NAME + ","
                + "(" + base + " - " + BASE_COLUMN_NAME + ") AS base_difference"
                + " FROM " + TABLE_NAME
                + " WHERE " + GENOME_ID_COLUMN_NAME + "=" + genomeId + " AND base_difference >= 0"
                + " ORDER BY base_difference ASC"
                + " LIMIT 1";

        return (GenomePoint) fileDatabaseDriver.executeCustomQuery(sql, resultSet -> {
            try {
                if (!resultSet.next()) {
                    throw new SQLException("No genome index point found in database.");
                }
                return new GenomePoint(
                        genomeId,
                        base,
                        resultSet.getInt(NODE_ID_COLUMN_NAME),
                        resultSet.getInt("base_difference"));
            } catch (final SQLException e) {
                LOGGER.error("Unable to retrieve closest node to base " + base + " in genome " + genomeId + ".", e);
                return null;
            }
        });
    }
}
