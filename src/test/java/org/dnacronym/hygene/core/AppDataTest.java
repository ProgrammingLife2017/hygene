package org.dnacronym.hygene.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit tests for {@link AppData}.
 */
final class AppDataTest {
    private static final String TEST_FILE_NAME = "appdata-test.txt";


    @AfterAll
    static void tearDown() throws IOException {
        final File file = AppData.getInstance().getFile(TEST_FILE_NAME);

        java.nio.file.Files.deleteIfExists(file.toPath());
    }


    @Test
    void testInstanceRemainsTheSame() throws FileNotFoundException {
        final AppData files1 = AppData.getInstance();
        final AppData files2 = AppData.getInstance();

        assertThat(files1 == files2).isTrue();
    }

    @Test
    void testPutGetAppData() throws IOException {
        final String testData = "Computer science is no more about computers than astronomy is about telescopes. "
                + "- Edsger Dijkstra\n";
        AppData.getInstance().put(TEST_FILE_NAME, testData);

        assertThat(AppData.getInstance().read(TEST_FILE_NAME)).contains(testData);
    }
}
