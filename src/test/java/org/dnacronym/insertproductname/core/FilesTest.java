package org.dnacronym.insertproductname.core;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FilesTest {
    @Test
    final void testInstanceRemainsTheSame() throws FileNotFoundException {
        Files files1 = Files.getInstance();
        Files files2 = Files.getInstance();

        assertThat(files1 == files2).isTrue();
    }

    @Test
    final void testGetResourceUrl() throws FileNotFoundException {
        assertThat(Files.getInstance().getResourceUrl("/gfa/simple.gfa").toString())
                .contains("/gfa/simple.gfa");
    }

    @Test
    final void testGetResourceUrlForNonExistingFile() {
        final Throwable e = catchThrowable(() -> Files.getInstance().getResourceUrl("does-not-exist"));

        assertThat(e).isInstanceOf(FileNotFoundException.class);
    }
}
