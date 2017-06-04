package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;


/**
 * Class representing the database corresponding to a GFA file.
 */
@SuppressWarnings("initialization") // due to setup actions that need to be executed in the constructor
public final class FileDatabase implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(FileDatabase.class);

    static final int DB_VERSION = 6;

    private final String fileName;
    private FileDatabaseDriver fileDatabaseDriver;
    private FileMetadata fileMetadata;
    private FileBookmarks fileBookmarks;
    private FileGenomeIndex fileGenomeIndex;


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
        initializeDatabase();

        if (databaseAlreadyExisted) {
            try {
                fileMetadata.verifyMetadata();
            } catch (final IOException e) {
                LOGGER.info("Rebuilding file database...", e);
                Files.delete(new File(fileName + FileDatabaseDriver.DB_FILE_EXTENSION).toPath());
                initializeDatabase();
                setUpDatabase();
            }
        } else {
            setUpDatabase();
        }
    }

    /**
     * Initializes the file database.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    private void initializeDatabase() throws SQLException {
        fileDatabaseDriver = new FileDatabaseDriver(fileName);
        fileMetadata = new FileMetadata(this);

        fileBookmarks = new FileBookmarks(this);
        fileGenomeIndex = new FileGenomeIndex(this);
    }


    /**
     * Sets up the tables of the database.
     *
     * @throws SQLException in the case of an error during SQL operations
     * @throws IOException  in the case of an error during IO operations
     */
    private void setUpDatabase() throws SQLException, IOException {
        fileDatabaseDriver.setUpTable(fileMetadata.getTable());
        fileMetadata.storeMetadata();

        fileDatabaseDriver.setUpTable(fileBookmarks.getTable());
        fileDatabaseDriver.setUpTable(fileGenomeIndex.getTable());
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
     * Returns the {@link FileMetadata} instance.
     *
     * @return the {@link FileMetadata} instance
     */
    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    /**
     * Returns the {@link FileBookmarks} instance.
     *
     * @return the {@link FileBookmarks} instance
     */
    public FileBookmarks getFileBookmarks() {
        return fileBookmarks;
    }

    /**
     * Returns the file genome index.
     *
     * @return the file genome index
     */
    public FileGenomeIndex getFileGenomeIndex() {
        return fileGenomeIndex;
    }

    @Override
    public void close() throws SQLException {
        fileDatabaseDriver.close();
    }
}
