package org.dnacronym.hygene.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit tests for {@link AppData}.
 */
final class AppDataTest {
    private static final String TEST_FILE_NAME = "appdata-test.txt";


    @AfterAll
    static void tearDown() throws IOException {
        final File file = AppData.getFile(TEST_FILE_NAME);

        java.nio.file.Files.deleteIfExists(file.toPath());
    }


    @Test
    void testPutGetAppData() throws IOException {
        final String testData = "Computer science is no more about computers than astronomy is about telescopes. "
                + "- Edsger Dijkstra\n";
        AppData.put(TEST_FILE_NAME, testData);

        assertThat(AppData.read(TEST_FILE_NAME)).contains(testData);
    }
}
