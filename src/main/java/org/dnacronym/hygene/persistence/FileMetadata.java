package org.dnacronym.hygene.persistence;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * Class responsible for storing and checking metadata on a file-level.
 */
final class FileMetadata {
    private static final String DB_VERSION = "0.0.1";

    private static final String TABLE_NAME = "global";
    private static final String KEY_COLUMN_NAME = "global_key";
    private static final String VALUE_COLUMN_NAME = "global_value";
    private static final String VERSION_KEY_NAME = "version";
    private static final String DIGEST_KEY_NAME = "digest";

    private final FileDatabase fileDatabase;
    private final DatabaseDriver databaseDriver;


    /**
     * Constructs a FileMetadata instance.
     *
     * @param fileDatabase the file to be
     */
    public FileMetadata(final FileDatabase fileDatabase) {
        this.fileDatabase = fileDatabase;
        this.databaseDriver = fileDatabase.getDatabaseDriver();
    }


    /**
     * Generates a {@link DatabaseTable} instance for the table holding metadata.
     *
     * @return a {@link DatabaseTable} instance for the table holding metadata
     */
    DatabaseTable getTable() {
        final DatabaseTable globalTable = new DatabaseTable(TABLE_NAME);
        globalTable.addColumn(KEY_COLUMN_NAME, "string");
        globalTable.addColumn(VALUE_COLUMN_NAME, "string");

        return globalTable;
    }

    /**
     * Stores all metadata key-value pairs in the DB.
     *
     * @throws IOException  in the case of an error during IO operations
     * @throws SQLException in the case of an error during SQL operations
     */
    void storeMetadata() throws IOException, SQLException {
        databaseDriver.insertRow(TABLE_NAME, Arrays.asList(VERSION_KEY_NAME, DB_VERSION));
        databaseDriver.insertRow(TABLE_NAME, Arrays.asList(DIGEST_KEY_NAME, computeFileDigest()));
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
            throw new IncompatibleDatabaseVersionException("File database version not compatible with this version of "
                    + "the program.");
        }

        if (!checkFileDigest()) {
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
    private String getMetadataValue(final String keyName) throws SQLException {
        return databaseDriver.getSingleValue(TABLE_NAME, KEY_COLUMN_NAME, keyName, VALUE_COLUMN_NAME);
    }

    /**
     * Check whether the digest stored in the DB and the digest of the file on disk correspond.
     *
     * @return {@code} true iff. the two digests are equal
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
     * <p>
     * Assumes the Semantic Versioning standard and compares only the 'major' components of the two versions.
     *
     * @return {@code true} iff. the versions are compatible
     * @throws SQLException in the case of an error during SQL operations
     */
    private boolean checkVersionCompatibility() throws SQLException {
        final int currentMajorVersion = Integer.parseInt(DB_VERSION.split(".")[0]);
        final int fileMajorVersion = Integer.parseInt(getMetadataValue(VERSION_KEY_NAME).split(".")[0]);

        return currentMajorVersion == fileMajorVersion;
    }

    /**
     * Computes the digest of the contents of the file this database belongs to.
     *
     * @return a digest, representing the contents of the file
     * @throws IOException in the case of an error during IO operations
     */
    private String computeFileDigest() throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(new File(fileDatabase.getFileName()));
        final String digest = DigestUtils.sha512Hex(fileInputStream);
        fileInputStream.close();

        return digest;
    }
}
