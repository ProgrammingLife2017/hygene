package org.dnacronym.hygene.persistence;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;


/**
 * Class responsible for storing and checking metadata on a file-level.
 */
final class FileMetadata {
    private static final String DB_VERSION = "0.0.1";

    private static final String GLOBAL_TABLE_NAME = "global";
    private static final String VERSION_KEY_NAME = "version";
    private static final String DIGEST_KEY_NAME = "digest";

    private final FileDatabase fileDatabase;
    private final DatabaseDriver databaseDriver;


    public FileMetadata(FileDatabase fileDatabase) {
        this.fileDatabase = fileDatabase;
        this.databaseDriver = fileDatabase.getDatabaseDriver();
    }


    DatabaseTable getTable() {
        final DatabaseTable globalTable = new DatabaseTable(GLOBAL_TABLE_NAME);
        globalTable.addColumn("global_key", "string");
        globalTable.addColumn("global_value", "string");

        return globalTable;
    }

    void setGlobalMetadata() throws SQLException, IOException {
        databaseDriver.insertRow(GLOBAL_TABLE_NAME, Arrays.asList(VERSION_KEY_NAME, DB_VERSION));
        databaseDriver.insertRow(GLOBAL_TABLE_NAME, Arrays.asList(DIGEST_KEY_NAME, computeFileDigest()));
    }

    private void verifyGlobalMetadata() throws IOException, SQLException {
        if (!checkVersionCompatibility()) {
            throw new IncompatibleDatabaseVersionException("File database version not compatible with this version of "
                    + "the program.");
        }

        if (!checkFileDigest()) {
            throw new FileDigestDatabaseException("Stored digest and computed digest of file did not match.");
        }
    }

    private String getGlobalMetadataValue(final String keyName) throws SQLException {
        final Statement statement = databaseDriver.getConnection().createStatement();

        final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + GLOBAL_TABLE_NAME
                + " WHERE global_key='" + keyName + "'");

        if (!resultSet.next()) {
            throw new SQLException("Expected global metadata values to be stored.");
        }
        final String value = resultSet.getString("global_value");

        statement.close();

        return value;
    }

    private boolean checkFileDigest() throws IOException, SQLException {
        final String currentDigest = computeFileDigest();

        return getGlobalMetadataValue(DIGEST_KEY_NAME).equals(currentDigest);
    }

    /**
     * Checks whether the version of the database specification stored in the file database is compatible with the
     * current version.
     * <p>
     * Assumes the Semantic Versioning standard and compares only the 'major' components of the two versions.
     *
     * @return {@code true} iff. the versions are compatible
     * @throws SQLException in the case of an error during IO operations
     */
    private boolean checkVersionCompatibility() throws SQLException {
        final int currentMajorVersion = Integer.parseInt(DB_VERSION.split(".")[0]);
        final int fileMajorVersion = Integer.parseInt(getGlobalMetadataValue(VERSION_KEY_NAME).split(".")[0]);

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
