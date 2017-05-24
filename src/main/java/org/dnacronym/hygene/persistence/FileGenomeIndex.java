package org.dnacronym.hygene.persistence;

/**
 * Class responsible for storing and retrieving genome coordinate system index points.
 */
final class FileGenomeIndex {
    static final String TABLE_NAME = "genome_index";

    private static final String GENOME_ID_COLUMN_NAME = "genome_id";
    private static final String BASE_COLUMN_NAME = "base";
    private static final String NODE_ID_COLUMN_NAME = "node_id";

    private final FileDatabase fileDatabase;
    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a {@link FileGenomeIndex} instance.
     *
     * @param fileDatabase the database to contain that genome index
     */
    FileGenomeIndex(final FileDatabase fileDatabase) {
        this.fileDatabase = fileDatabase;
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding genome index points.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding genome index points
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(GENOME_ID_COLUMN_NAME, "INTEGER");
        globalTable.addColumn(BASE_COLUMN_NAME, "INTEGER");
        globalTable.addColumn(NODE_ID_COLUMN_NAME, "INTEGER");

        return globalTable;
    }
}
