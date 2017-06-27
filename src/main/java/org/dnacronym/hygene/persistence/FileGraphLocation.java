package org.dnacronym.hygene.persistence;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Represents a {@link FileGraphLocation}.
 */
public class FileGraphLocation {
    static final String TABLE_NAME = "graph_location";

    private static final String CENTER_NODE_ID_COLUMN_NAME = "node_id";
    private static final String RADIUS_COLUMN_NAME = "radius";

    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a new {@link FileBookmarks} instance.
     *
     * @param fileDatabase the {@link FileDatabase} instance to read from and write to
     */
    public FileGraphLocation(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding bookmarks.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding bookmarks
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(CENTER_NODE_ID_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(RADIUS_COLUMN_NAME, ColumnType.INTEGER);

        return globalTable;
    }

    /**
     * Checks if there is a stored location.
     *
     * @return true if there is a stored location
     * @throws SQLException in the case of an error during SQL operations
     */
    public boolean locationIsStored() throws SQLException {
        return fileDatabaseDriver.hasRow(TABLE_NAME, "'1'", "1");
    }

    /**
     * Retrieves the stored center node id.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    public int getCenterNodeId() throws SQLException {
        return Integer.parseInt(
                fileDatabaseDriver.getSingleValue(TABLE_NAME, "'1'", "1", CENTER_NODE_ID_COLUMN_NAME)
        );
    }

    /**
     * Retrieves the stored radius.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    public int getRadius() throws SQLException {
        return Integer.parseInt(
                fileDatabaseDriver.getSingleValue(TABLE_NAME, "'1'", "1", RADIUS_COLUMN_NAME)
        );
    }

    /**
     * Stores the current location in the database.
     *
     * @param centerNodeId the center node id
     * @param radius       the radius
     * @throws SQLException in the case of an error during SQL operations
     */
    public void store(final int centerNodeId, final int radius) throws SQLException {
        fileDatabaseDriver.deleteAllFromTable(TABLE_NAME);

        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(
                Integer.toString(centerNodeId),
                Integer.toString(radius)
        ));
    }
}
