package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@code GFAParser}s.
 */
class GFAParserTest {
    private GFAParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new GFAParser();
    }


    @Test
    void testParseEmpty() throws ParseException {
        final Assembly assembly = parse("");

        assertThat(assembly.getLinks()).isEmpty();
        assertThat(assembly.getSegments()).isEmpty();
    }

    @Test
    void testUnknownRecordType() {
        final String gfa = "ç 4 + 2 + 1M";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testSegmentWithMissingSequence() {
        final String gfa = "S name";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
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
        final Assembly assembly = parse(gfa);

        assertThat(assembly.getSegments()).hasSize(2);
        assertThat(assembly.getLinks()).hasSize(1);
    }


    private String replaceSpacesWithTabs(final String string) {
        return string.replaceAll(" ", "\t");
    }

    private Assembly parse(final String gfa) throws ParseException {
        return parser.parse(replaceSpacesWithTabs(gfa));
    }
}
