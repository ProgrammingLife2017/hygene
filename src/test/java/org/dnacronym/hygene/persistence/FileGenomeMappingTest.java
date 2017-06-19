package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@link FileGenomeMapping}.
 */
class FileGenomeMappingTest extends FileDatabaseTestBase {
    private FileDatabase fileDatabase;
    private FileGenomeMapping fileGenomeMapping;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        fileGenomeMapping = new FileGenomeMapping(fileDatabase);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        super.tearDown();
    }

    @Test
    void testGetMappings() throws SQLException {
        assertThat(fileGenomeMapping.getMappings().size()).isEqualTo(0);
    }

    @Test
    void testRestoreGraph() throws SQLException, GfaParseException, IOException {
        Files.deleteIfExists(Paths.get(GFA_FILE_NAME + ".hygenecache"));

        new GfaFile(GFA_FILE_NAME).parse(mock(ProgressUpdater.class));

        assertThat(fileGenomeMapping.getMappings().size()).isEqualTo(2);
    }

    @Test
    void testAddMappings1() throws SQLException {
        fileGenomeMapping.addMapping("1", "sample.fasta");
        assertThat(fileGenomeMapping.getMappings().size()).isEqualTo(1);
        assertThat(fileGenomeMapping.getMappings().get("1")).isEqualTo("sample.fasta");
    }

    @Test
    void testAddMappings2() throws SQLException {
        final Map<String, String> mappings = new HashMap<>();
        mappings.put("1", "first.fasta");
        mappings.put("2", "second.fasta");
        fileGenomeMapping.addMapping(mappings);
        assertThat(fileGenomeMapping.getMappings().size()).isEqualTo(2);
        assertThat(fileGenomeMapping.getMappings().get("1")).isEqualTo("first.fasta");
        assertThat(fileGenomeMapping.getMappings().get("2")).isEqualTo("second.fasta");
    }

    @Test
    void testAddMappingsException() throws SQLException {
        fileDatabase.close();
        assertThrows(SQLException.class, () -> {
            fileGenomeMapping.addMapping("1", "sample.fasta");
        });
    }

    @Test
    void testGetMappingsException() throws SQLException {
        fileDatabase.close();
        assertThrows(SQLException.class, () -> {
            assertThat(fileGenomeMapping.getMappings().size()).isEqualTo(0);
        });
    }
}
