package org.dnacronym.hygene.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Class responsible for storing and retrieving genome coordinate system index points.
 */
public final class FileGenomeMapping {
    private static final Logger LOGGER = LogManager.getLogger(FileGenomeMapping.class);

    static final String TABLE_NAME = "genome_mappings";

    private static final String GENOME_NAME_COLUMN_NAME = "genome_name";
    private static final String GENOME_MAPPING_COLUMN_NAME = "genome_mapping";

    private final FileDatabaseDriver fileDatabaseDriver;


    /**
     * Constructs a {@link FileGenomeIndex} instance.
     *
     * @param fileDatabase the database to contain that genome index
     */
    public FileGenomeMapping(final FileDatabase fileDatabase) {
        this.fileDatabaseDriver = fileDatabase.getFileDatabaseDriver();
    }


    /**
     * Generates a {@link FileDatabaseTable} instance for the table holding genome mappings.
     *
     * @return a {@link FileDatabaseTable} instance for the table holding genome mappings
     */
    FileDatabaseTable getTable() {
        final FileDatabaseTable globalTable = new FileDatabaseTable(TABLE_NAME);
        globalTable.addColumn(GENOME_NAME_COLUMN_NAME, ColumnType.TEXT);
        globalTable.addColumn(GENOME_MAPPING_COLUMN_NAME, ColumnType.TEXT);

        return globalTable;
    }


    /**
     * Adds a new genome mapping.
     *
     * @param genomeName    the name of the genome
     * @param genomeMapping the genome's mapping
     * @throws SQLException in the case of an error during SQL operations
     */
    public void addMapping(final String genomeName, final String genomeMapping) throws SQLException {
        fileDatabaseDriver.insertRow(TABLE_NAME, Arrays.asList(
                genomeName,
                genomeMapping
        ));
    }

    /**
     * Adds a new set of genome mappings.
     *
     * @param genomeMappings the genome mappings
     * @throws SQLException the sql exception
     */
    public void addMapping(final Map<String, String> genomeMappings) throws SQLException {
        genomeMappings.forEach((n, m) -> {
            try {
                addMapping(n, m);
            } catch (SQLException e) {
                LOGGER.error("Could not store genome mappings.", e);
            }
        });
    }

    /**
     * Returns a {@link Map} of genome containing genome mappings.
     *
     * @return the mappings
     * @throws SQLException in the case of an error during SQL operations
     */
    public Map<String, String> getMappings() throws SQLException {
        final Map<String, String> genomeMappings = new HashMap<>();

        fileDatabaseDriver.forEachRow(TABLE_NAME, row -> {
            try {
                final String mapping = row.getString(GENOME_MAPPING_COLUMN_NAME);
                final String name = row.getString(GENOME_NAME_COLUMN_NAME);

                if (mapping != null && name != null) {
                    genomeMappings.put(name, mapping);
                }
            } catch (SQLException e) {
                LOGGER.error("An error occurred while retrieving genome mappings for the database.", e);
            }
        });

        return genomeMappings;
    }
}
