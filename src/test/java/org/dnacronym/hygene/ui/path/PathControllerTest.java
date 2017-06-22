package org.dnacronym.hygene.ui.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit test for {@link PathController}
 */
final class PathControllerTest {
    private PathController pathController;
    private GenomePath genomePath;


    @BeforeEach
    public void beforeEach() {
        pathController = new PathController();
        genomePath = new GenomePath("1", "Hello-World-42.fasta");
    }


    @Test
    void testSubstringNull() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getSubstringPredicate(null, false);
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testSubstringEmptyString() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getSubstringPredicate("", false);
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testSubstringBasicWord() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getSubstringPredicate("hello", false);
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testSubstringBasicWordWithCaps() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getSubstringPredicate("world", true);
        assertThat(genomePathPredicate.test(genomePath)).isFalse();
    }

    @Test
    void testSubstringBasicWordWithCapsNegative() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getSubstringPredicate("World", true);
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testRegexNull() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getRegexPredicate(null);
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testRegexEmptyString() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getRegexPredicate("");
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }

    @Test
    void testRegexBasicWord() throws Exception {
        final Predicate<GenomePath> genomePathPredicate = pathController.getRegexPredicate(".*");
        assertThat(genomePathPredicate.test(genomePath)).isTrue();
    }
}
