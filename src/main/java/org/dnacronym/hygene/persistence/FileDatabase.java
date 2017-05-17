package org.dnacronym.hygene.persistence;


import java.io.File;
import java.sql.SQLException;

/**
 * Class representing the database corresponding to a file.
 */
public class FileDatabase {
    private final String fileName;
    private final DatabaseDriver databaseDriver;
    private final FileMetadata fileMetadata;


    public FileDatabase(String fileName) throws SQLException {
        this.fileName = fileName;

        final boolean databaseAlreadyExisted = (new File(fileName + DatabaseDriver.DB_FILE_EXTENSION)).exists();
        this.databaseDriver = new DatabaseDriver(fileName);
        this.fileMetadata = new FileMetadata(this);
    }

    public String getFileName() {
        return fileName;
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
