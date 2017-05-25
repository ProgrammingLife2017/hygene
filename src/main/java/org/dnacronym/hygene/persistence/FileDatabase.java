package org.dnacronym.hygene.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Class representing the database corresponding to a GFA file.
 */
@SuppressWarnings("initialization") // due to setup actions that need to be executed in the constructor
public final class FileDatabase implements AutoCloseable {
    private final String fileName;
    private final FileDatabaseDriver fileDatabaseDriver;
    private final FileBookmarks fileBookmarks;


    /**
     * Constructs a {@link FileDatabase} instance.
     *
     * @param fileName the name / path of the GFA file that this database should correspond to
     * @throws SQLException in the case of an error during SQL operations
     * @throws IOException  in the case of an error during IO operations
     */
    public FileDatabase(final String fileName) throws SQLException, IOException {
        this.fileName = fileName;

        if (!new File(fileName).exists()) {
            throw new FileNotFoundException("GFA file not found.");
        }

        final boolean databaseAlreadyExisted = new File(fileName + FileDatabaseDriver.DB_FILE_EXTENSION).exists();
        fileDatabaseDriver = new FileDatabaseDriver(fileName);

        final FileMetadata fileMetadata = new FileMetadata(this);
        fileBookmarks = new FileBookmarks(this);

        if (databaseAlreadyExisted) {
            fileMetadata.verifyMetadata();
        } else {
            fileDatabaseDriver.setUpTable(fileMetadata.getTable());
            fileMetadata.storeMetadata();

            fileDatabaseDriver.setUpTable(fileBookmarks.getTable());
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
     * Returns the {@link FileDatabaseDriver}.
     *
     * @return the {@link FileDatabaseDriver}
     */
    FileDatabaseDriver getFileDatabaseDriver() {
        return fileDatabaseDriver;
    }

    /**
     * Returns the {@link FileBookmarks} instance.
     *
     * @return the {@link FileBookmarks} instance
     */
    public FileBookmarks getFileBookmarks() {
        return fileBookmarks;
    }

    @Override
    public void close() throws SQLException {
        fileDatabaseDriver.close();
    }
}
