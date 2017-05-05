package org.dnacronym.hygene.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@code GfaParser}s.
 */
class GfaParserTest {
    private GfaParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new GfaParser();
    }


    @Test
    void testParseEmpty() throws ParseException {
        final SequenceAlignmentGraph graph = parse("");

        assertThat(graph.getLinks()).isEmpty();
        assertThat(graph.getSegments()).isEmpty();
    }

    @Test
    void testUnknownRecordType() {
        final String gfa = "ç 4 + 2 + 1M";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testIgnoredRecordTypes() throws ParseException {
        final String gfa = "H header\nC containment\nP path";
        assertThat(parse(gfa)).isNotNull();
    }

    @Test
    void testSegmentWithMissingSequence() {
        final String gfa = "S name";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testSegmentContents() throws ParseException {
        final String gfa = "S name contents";
        final SequenceAlignmentGraph graph = parse(gfa);

        assertThat(graph.getSegment("name").getSequence()).isEqualTo("contents");
    }

    @Test
    void testLinkWithMissingOverlap() {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 +";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testLinkWithNonIntegerOverlap() {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + ç";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testSizes() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M";
        final SequenceAlignmentGraph graph = parse(gfa);

        assertThat(graph.getSegments()).hasSize(2);
        assertThat(graph.getLinks()).hasSize(1);
    }


    private String replaceSpacesWithTabs(final String string) {
        return string.replaceAll(" ", "\t");
    }

    private SequenceAlignmentGraph parse(final String gfa) throws ParseException {
        return parser.parse(replaceSpacesWithTabs(gfa));
    }
}
