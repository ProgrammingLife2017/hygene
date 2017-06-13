package org.dnacronym.hygene.persistence;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * Class responsible for storing and checking metadata on a file-level.
 */
final class FileMetadata {
    static final String TABLE_NAME = "global";
    static final String VERSION_KEY_NAME = "version";
    static final String DIGEST_KEY_NAME = "digest";
    static final String IS_INDEXED_KEY_NAME = "is_indexed";

    private static final String KEY_COLUMN_NAME = "global_key";
    private static final String VALUE_COLUMN_NAME = "global_value";

    private final FileDatabase fileDatabase;
    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a FileMetadata instance.
     *
     * @param fileDatabase the database to contain that metadata
     */
    FileMetadata(final FileDatabase fileDatabase) {
        this.fileDatabase = fileDatabase;
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding metadata.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding metadata
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(KEY_COLUMN_NAME, ColumnType.TEXT);
        globalTable.addColumn(VALUE_COLUMN_NAME, ColumnType.TEXT);

        return globalTable;
    }

    /**
     * Stores all metadata key-value pairs in the DB.
     *
     * @throws IOException  in the case of an error during IO operations
     * @throws SQLException in the case of an error during SQL operations
     */
    void storeMetadata() throws IOException, SQLException {
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(VERSION_KEY_NAME, String.valueOf(FileDatabase
                .DB_VERSION)));
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(DIGEST_KEY_NAME, computeFileDigest()));
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(IS_INDEXED_KEY_NAME, "false"));
    }

    /**
     * Verifies the consistency and validity of the metadata present in the DB.
     * <p>
     * Throws corresponding exceptions if any of these verifications fail.
     *
     * @throws IOException  in the case of an error during IO operations, or in the case of verification failure(s)
     * @throws SQLException in the case of an error during SQL operations
     */
    void verifyMetadata() throws IOException, SQLException {
        if (!checkVersionCompatibility()) {
            fileDatabase.close();
            throw new IncompatibleDatabaseVersionException("File database version not compatible with this version of "
                    + "the program.");
        }

        if (!checkFileDigest()) {
            fileDatabase.close();
            throw new FileDigestDatabaseException("Stored digest and computed digest of file did not match.");
        }
    }

    /**
     * Fetches the value corresponding to the given key.
     *
     * @param keyName the name of the key
     * @return the value associated with that key in the file database
     * @throws SQLException in the case of an error during SQL operations
     */
    String getMetadataValue(final String keyName) throws SQLException {
        return fileDatabaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME, keyName, VALUE_COLUMN_NAME);
    }

    /**
     * Check whether the digest stored in the DB and the digest of the file on disk correspond.
     *
     * @return {@code true} iff. the two digests are equal
     * @throws IOException  in the case of an error during IO operations
     * @throws SQLException in the case of an error during SQL operations
     */
    private boolean checkFileDigest() throws IOException, SQLException {
        final String currentDigest = computeFileDigest();

        return getMetadataValue(DIGEST_KEY_NAME).equals(currentDigest);
    }

    /**
     * Checks whether the version of the database specification stored in the file database is compatible with the
     * current version.
     *
     * @return {@code true} iff. the versions are compatible
     * @throws SQLException                         in the case of an error during SQL operations
     * @throws IncompatibleDatabaseVersionException in the case of an incompatible DB format
     */
    private boolean checkVersionCompatibility() throws SQLException, IncompatibleDatabaseVersionException {
        final String fileVersionString = getMetadataValue(VERSION_KEY_NAME);

        final int fileVersion;
        try {
            fileVersion = Integer.parseInt(fileVersionString);
        } catch (final NumberFormatException e) {
            throw new IncompatibleDatabaseVersionException("Database version format incompatible, found: "
                    + fileVersionString + ", expected an integer.", e);
        }
        return FileDatabase.DB_VERSION == fileVersion;
    }

    /**
     * Computes the digest of the contents of the file this database belongs to.
     *
     * @return a digest, representing the contents of the file
     * @throws IOException in the case of an error during IO operations
     */
    private String computeFileDigest() throws IOException {
        final BasicFileAttributes attr =
                Files.readAttributes(Paths.get(fileDatabase.getFileName()), BasicFileAttributes.class);

        return DigestUtils.sha512Hex(fileDatabase.getFileName() + attr.lastModifiedTime() + attr.size());
    }

    boolean isIndexed() throws IOException, SQLException {
        return Boolean.parseBoolean(
                fileDatabaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME, IS_INDEXED_KEY_NAME, VALUE_COLUMN_NAME)
        );
    }

    void setIndexedState(final boolean isIndexed) throws SQLException {
        fileDatabaseDriver.setSingleValue(TABLE_NAME, KEY_COLUMN_NAME, IS_INDEXED_KEY_NAME, VALUE_COLUMN_NAME,
                String.valueOf(isIndexed));
    }
}
