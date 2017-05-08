package org.dnacronym.hygene.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FilesTest {
    private static final String TEST_FILE_NAME = "appdata-test.txt";


    @AfterAll
    static void tearDown() throws IOException {
        final File file = Files.getInstance().getAppDataFile(TEST_FILE_NAME);

        if (file.exists() && !file.delete()) {
            throw new IOException("File has not been created in any of the tests");
        }
    }


    @Test
    void testInstanceRemainsTheSame() throws FileNotFoundException {
        Files files1 = Files.getInstance();
        Files files2 = Files.getInstance();

        assertThat(files1 == files2).isTrue();
    }

    @Test
    void testGetResourceUrl() throws FileNotFoundException {
        assertThat(Files.getInstance().getResourceUrl("/gfa/simple.gfa").toString())
                .contains("/gfa/simple.gfa");
    }

    @Test
    void testGetResourceUrlForNonExistingFile() {
        final Throwable e = catchThrowable(() -> Files.getInstance().getResourceUrl("does-not-exist"));

        assertThat(e).isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void testPutGetAppData() throws IOException {
        final String testData = "Computer science is no more about computers than astronomy is about telescopes. "
                + "- Edsger Dijkstra\n";
        Files.getInstance().putAppData(TEST_FILE_NAME, testData);

        assertThat(Files.getInstance().getAppData(TEST_FILE_NAME)).contains(testData);
    }
}
