package org.dnacronym.hygene.persistence;

import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class responsible for reading and writing from the file-database.
 */
final class DatabaseDriver {
    private static final String DB_FILE_EXTENSION = ".db";

    private final String fileName;
    private final Connection connection;
    private final List<DatabaseTable> tables;


    /**
     * Constructs a DatabaseDriver.
     *
     * @param fileName the file name of the corresponding GFA-file
     * @param tables   the list of tables (assumed to have at least one element)  @throws SQLException in the case of erroneous SQL behaviour
     */
    DatabaseDriver(final String fileName, List<DatabaseTable> tables) throws SQLException {
        this.fileName = fileName;
        this.tables = tables;

        final boolean databaseAlreadyExisted = (new File(fileName + DB_FILE_EXTENSION)).exists();
        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName + DB_FILE_EXTENSION);

        if (!databaseAlreadyExisted) {
            setupTables();
        }
    }


    /**
     * Creates the given tables in the database.
     *
     * @throws SQLException in the case of erroneous SQL behaviour
     */
    private void setupTables() throws SQLException {
        final Statement statement = connection.createStatement();

        for (DatabaseTable table : tables) {
            final String columnList = String.join(", ", table.getColumns().stream().map(
                    (Pair<String, String> column) -> column.getKey() + " " + column.getValue()
            ).collect(Collectors.toList()));

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table.getName() + "(" + columnList + ")");
        }
    }

    /**
     * Computes the digest of the contents of the file this database belongs to.
     *
     * @return a digest, representing the contents of the file
     * @throws IOException in the case of errors during IO operations
     */
    private String computeFileDigest() throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(new File(fileName));
        final String digest = DigestUtils.sha512Hex(fileInputStream);
        fileInputStream.close();

        return digest;
    }
}
