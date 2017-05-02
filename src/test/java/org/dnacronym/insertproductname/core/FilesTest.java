package org.dnacronym.insertproductname.core;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FilesTest {
    @Test
    void testGetResourceUrl() throws FileNotFoundException {

        assertThat(Files.getInstance().getResourceUrl("/gfa/simple.gfa").toString())
                .contains("/gfa/simple.gfa");
    }
}
