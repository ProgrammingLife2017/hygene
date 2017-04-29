package org.dnacronym.insertproductname;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ExampleTest {
    @Test
    void testTwoStringWithSameContentsAreEqual() {
        String someString = "123";

        assertThat(someString).isEqualTo("123");
        assertThat(someString).containsOnlyDigits();
    }

    @Test
    void testSomethingWithMocks() {
        final Thing mockedThing = mock(Thing.class);
        when(mockedThing.returnTrue()).thenReturn(false);

        assertThat(mockedThing.returnTrue()).isFalse();
    }


    private class Thing {
        boolean returnTrue() {
            return true;
        }
    }
}
