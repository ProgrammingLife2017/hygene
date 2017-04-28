package org.dnacronym.insertproductname;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleTest {

    @Test
    void testTwoStringWithSameContentsAreEqual() {
        String someString = "123";

        assertThat(someString).isEqualTo("123");
        assertThat(someString).containsOnlyDigits();
    }

}
