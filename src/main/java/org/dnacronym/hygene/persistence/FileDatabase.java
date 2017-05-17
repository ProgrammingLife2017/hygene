package org.dnacronym.hygene.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Class representing the database corresponding to a file.
 */
@SuppressWarnings("initialization")
public final class FileDatabase {
    private final String fileName;
    private final DatabaseDriver databaseDriver;


    /**
     * Constructs a FileDatabase instance.
     *
     * @param fileName the name / path of the GFA file that this database should correspond to
     * @throws SQLException in the case of an error during SQL operations
     * @throws IOException  in the case of an error during IO operations
     */
    public FileDatabase(final String fileName) throws SQLException, IOException {
        this.fileName = fileName;

        final boolean databaseAlreadyExisted = (new File(fileName + DatabaseDriver.DB_FILE_EXTENSION)).exists();
        databaseDriver = new DatabaseDriver(fileName);

        final FileMetadata fileMetadata = new FileMetadata(this);

        if (!databaseAlreadyExisted) {
            databaseDriver.setupTable(fileMetadata.getTable());
            fileMetadata.storeMetadata();
        } else {
            fileMetadata.verifyMetadata();
        }
    }


    /**
     * Returns the file name.
     *
     * @return the file name
     */
    String getFileName() {
        return fileName;
    }

    /**
     * Returns the {@link DatabaseDriver}.
     *
     * @return the {@link DatabaseDriver}
     */
    DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
