package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Class responsible for storing and retrieving bookmarks.
 */
public final class FileBookmarks {
    static final String TABLE_NAME = "bookmarks";

    private static final Logger LOGGER = LogManager.getLogger(FileBookmarks.class);

    private static final String NODE_ID_COLUMN_NAME = "nodeId";
    private static final String BASE_OFFSET_COLUMN_NAME = "baseOffset";
    private static final String RADIUS_COLUMN_NAME = "radius";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a new {@link FileBookmarks} instance.
     *
     * @param fileDatabase the {@link FileDatabase} instance to read from and write to
     */
    public FileBookmarks(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding bookmarks.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding bookmarks
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(NODE_ID_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(BASE_OFFSET_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(RADIUS_COLUMN_NAME, ColumnType.INTEGER);
        globalTable.addColumn(DESCRIPTION_COLUMN_NAME, ColumnType.TEXT);

        return globalTable;
    }

    /**
     * Retrieves all bookmarks from the file DB.
     *
     * @return the list of bookmarks
     * @throws SQLException in the case of an error during SQL operations
     */
    public List<Bookmark> getAll() throws SQLException {
        final List<Bookmark> bookmarks = new ArrayList<>();

        fileDatabaseDriver.forEachRow(TABLE_NAME, row -> {
            try {
                final Bookmark bookmark = new Bookmark(
                        row.getInt(NODE_ID_COLUMN_NAME),
                        row.getInt(BASE_OFFSET_COLUMN_NAME),
                        row.getInt(RADIUS_COLUMN_NAME),
                        Optional.ofNullable(row.getString(DESCRIPTION_COLUMN_NAME)).orElse("")
                );
                bookmarks.add(bookmark);
            } catch (final SQLException e) {
                LOGGER.error("Failed to load bookmark.", e);
            }
        });

        return bookmarks;
    }

    /**
     * Stores the given bookmarks in the table.
     * <p>
     * Any previously stored bookmarks are removed.
     *
     * @param bookmarks the list of bookmarks to be stored
     * @throws SQLException in the case of an error during SQL operations
     */
    public void storeAll(final List<Bookmark> bookmarks) throws SQLException {
        fileDatabaseDriver.deleteAllFromTable(TABLE_NAME);

        bookmarks.forEach(bookmark -> {
            final List<String> values = Arrays.asList(
                    String.valueOf(bookmark.getNodeId()),
                    String.valueOf(bookmark.getBaseOffset()),
                    String.valueOf(bookmark.getRadius()),
                    bookmark.getDescription()
            );

            try {
                fileDatabaseDriver.insertRow(TABLE_NAME, values);
            } catch (final SQLException e) {
                LOGGER.error("Storing of bookmark failed.", e);
            }
        });
    }
}
