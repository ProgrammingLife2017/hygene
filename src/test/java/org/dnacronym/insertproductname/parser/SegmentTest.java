package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@code Segment}s.
 */
class SegmentTest {
    @Test
    void testNullName() {
        final Throwable e = catchThrowable(() -> new Segment(null, "KQpD5PbkOovX21f1losl"));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNullSequence() {
        final Throwable e = catchThrowable(() -> new Segment("xpynu2oSI4xEMGya0aO7", null));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetName() {
        final Segment segment = new Segment("94ShLJ3ytpoXBiQxaxJ1", "aTeYVYR38z6AWbV2OgGh");
        assertThat(segment.getName()).isEqualTo("94ShLJ3ytpoXBiQxaxJ1");
    }

    @Test
    void testGetSequence() {
        final Segment segment = new Segment("VWTkg5SAIpPwMNH5e3zQ", "dLiPH1qrIMISxxFqfbaa");
        assertThat(segment.getSequence()).isEqualTo("dLiPH1qrIMISxxFqfbaa");
    }

    @Test
    void testGetLength() {
        final String sequence = "vaKlWKSE0TAcFFjqfp4N";
        final Segment segment = new Segment("gqkrQuzvtvjZLdOa8hDV", sequence);

        assertThat(segment.getLength()).isEqualTo(sequence.length());
    }

    @Test
    void testGetReversedSequence() {
        final Segment segment = new Segment("xN3zLSN73ulhUMZ0zPac", "QXr99QbUGX521Gfbbh4B");
        assertThat(segment.getReversedSequence()).isEqualTo("B4hbbfG125XGUbQ99rXQ");
    }

    @Test
    void testGetReversedSequenceEmpty() {
        final Segment segment = new Segment("FKjEclNC4G25BfsepUQS", "");
        assertThat(segment.getReversedSequence()).isEqualTo("");
    }
}
