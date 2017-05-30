package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.FileDatabaseDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;


/**
 * Test suite for the {@link CoordinateSystemIndex} class.
 */
class CoordinateSystemIndexTest {
    private static int TEST_BASE_CACHE_INTERVAL = 1;
    private static String TEST_GFA_FILE_NAME = "test.gfa";

    private CoordinateSystemIndex coordinateSystemIndex;
    private FileDatabase fileDatabase;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        fileDatabase = new FileDatabase(TEST_GFA_FILE_NAME);
        coordinateSystemIndex = new CoordinateSystemIndex(new GfaFile(TEST_GFA_FILE_NAME), fileDatabase);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        Files.deleteIfExists(new File(TEST_GFA_FILE_NAME + FileDatabaseDriver.DB_FILE_EXTENSION).toPath());
    }


    @Test
    void getClosestNodeId() {
    }
}
