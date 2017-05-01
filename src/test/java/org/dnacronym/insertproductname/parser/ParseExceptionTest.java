package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@code ParseException}s.
 */
class ParseExceptionTest {
    @Test
    void testEmptyConstructorMessage() {
        final ParseException e = new ParseException();
        assertThat(e.getMessage()).isNull();
    }

    @Test
    void testEmptyConstructorCause() {
        final ParseException e = new ParseException();
        assertThat(e.getCause()).isNull();
    }

    @Test
    void testMessageConstructorMessage() {
        final String message = "tUHZqHiQ2JzuZMMqQl0C";
        final ParseException e = new ParseException(message);

        assertThat(e.getMessage()).isEqualTo(message);
    }

    @Test
    void testMessageConstructorCause() {
        final String message = "TQRA66EsLPVJgdlyyDkz";
        final ParseException e = new ParseException(message);

        assertThat(e.getCause()).isEqualTo(null);
    }

    @Test
    void testCauseConstructorMessage() {
        final Throwable throwable = mock(Throwable.class);
        final ParseException e = new ParseException(throwable);

        assertThat(e.getMessage()).isEqualTo(throwable.toString());
    }

    @Test
    void testCauseConstructorCause() {
        final Throwable throwable = mock(Throwable.class);
        final ParseException e = new ParseException(throwable);

        assertThat(e.getCause()).isEqualTo(throwable);
    }

    @Test
    void testBothConstructorMessage() {
        final String message = "gsaJ0GY2fB9EeAfmZ8ug";
        final Throwable throwable = mock(Throwable.class);
        final ParseException e = new ParseException(message, throwable);

        assertThat(e.getMessage()).isEqualTo(message);
    }

    @Test
    void testBothConstructorCause() {
        final String message = "S5OPn8SfgAeSAvrcMFpw";
        final Throwable throwable = mock(Throwable.class);
        final ParseException e = new ParseException(message, throwable);

        assertThat(e.getCause()).isEqualTo(throwable);
    }
}
