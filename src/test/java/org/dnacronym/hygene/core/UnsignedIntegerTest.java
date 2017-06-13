package org.dnacronym.hygene.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link UnsignedInteger}.
 */
public final class UnsignedIntegerTest {
    @Test
    void itConvertsALongToAnUnsignedInteger() {
        assertThat(UnsignedInteger.fromLong(42L)).isEqualTo(Integer.MIN_VALUE + 42);
    }

    @Test
    void itConvertsATheLowestAcceptedLongToAnUnsignedInteger() {
        assertThat(UnsignedInteger.fromLong(0L)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void itConvertsALongAboveTheIntegerBoundaryToAnUnsignedInteger() {
        assertThat(UnsignedInteger.fromLong(Integer.MAX_VALUE + 1L)).isEqualTo(0);
    }

    @Test
    void itConvertsTheHighestAcceptedLongToAnUnsignedInteger() {
        assertThat(UnsignedInteger.fromLong(UnsignedInteger.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void itCannotConvertALongThatIsTooHigh() {
        final long tooHighLong = UnsignedInteger.MAX_VALUE + 1L;
        final Throwable throwable = catchThrowable(() -> UnsignedInteger.fromLong(tooHighLong));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable).hasMessageContaining("Number cannot be higher than 4294967295.");
    }

    @Test
    void itCannotConvertANegativeLongToAnUnsignedInteger() {
        final Throwable throwable = catchThrowable(() -> UnsignedInteger.fromLong(-1L));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable).hasMessageContaining("A negative number cannot be converted to an unsigned integer.");
    }

    @Test
    void itConvertsAnUnsignedIntegerToALong() {
        final int unsignedInteger = UnsignedInteger.fromLong(42L);

        assertThat(UnsignedInteger.toLong(unsignedInteger)).isEqualTo(42L);
    }

    @Test
    void itConvertsTheLowestUnsignedIntegerToALong() {
        final int unsignedInteger = UnsignedInteger.fromLong(0L);

        assertThat(UnsignedInteger.toLong(unsignedInteger)).isEqualTo(0L);
    }

    @Test
    void itConvertsTheHighestUnsignedIntegerToALong() {
        final int unsignedInteger = UnsignedInteger.fromLong(UnsignedInteger.MAX_VALUE);

        assertThat(UnsignedInteger.toLong(unsignedInteger)).isEqualTo(UnsignedInteger.MAX_VALUE);
    }
}
