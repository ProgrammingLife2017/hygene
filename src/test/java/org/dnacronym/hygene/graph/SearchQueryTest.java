package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;
import org.dnacronym.hygene.persistence.FileDatabaseDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link SearchQuery} class.
 */
class SearchQueryTest {
    private static final String GFA_TEST_FILE = "src/test/resources/gfa/query.gfa";

    private SearchQuery searchQuery;


    @BeforeEach
    void setUp() throws ParseException {
        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        gfaFile.parse(ProgressUpdater.DUMMY);
        searchQuery = new SearchQuery(gfaFile);
    }

    @AfterEach
    void afterEach() throws IOException {
        NewGfaParserFactory.setInstance(null);
        MetadataParserFactory.setInstance(null);
        Files.deleteIfExists(Paths.get(GFA_TEST_FILE + FileDatabaseDriver.DB_FILE_EXTENSION));
        Files.deleteIfExists(Paths.get(GFA_TEST_FILE + ".hygenecache"));
    }


    @Test
    void executeNameRegexQuery() throws ParseException {
        assertThat(searchQuery.executeNameRegexQuery("1[1-2]")).containsExactlyInAnyOrder(1, 2);
    }
}
