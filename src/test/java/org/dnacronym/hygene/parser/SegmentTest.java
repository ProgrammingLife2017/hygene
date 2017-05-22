package org.dnacronym.hygene.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link Segment}s.
 */
final class SegmentTest {
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
