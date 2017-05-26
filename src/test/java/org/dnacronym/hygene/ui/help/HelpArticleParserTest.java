package org.dnacronym.hygene.ui.help;

import org.dnacronym.hygene.core.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Unit test for {@link HelpArticleParser}.
 */
class HelpArticleParserTest {
    private static final String HELP_ARTICLES_TESTING_FILE = "/help/test_help_articles.xml";

    private HelpArticleParser helpArticleLoader;
    private List<HelpArticle> articles;


    @BeforeEach
    void setUp() throws FileNotFoundException {
        helpArticleLoader = new HelpArticleParser();
        articles = helpArticleLoader.parse(Files.getInstance().getResourceUrl(HELP_ARTICLES_TESTING_FILE).toString());
    }


    @Test
    void testParseNumberOfArticles() {
        assertThat(articles.size()).isEqualTo(2);
    }

    @Test
    void testParseArticleTitle() {
        assertThat(articles.get(0).getTitle()).isEqualTo("Mockito");
        assertThat(articles.get(1).getTitle()).isEqualTo("JUnit5");
    }

    @Test
    void testParseArticleContent() {
        assertThat(articles.get(0).getContent())
                .isEqualTo("Mockito is a mocking framework and used extensively by Hygene.");
        assertThat(articles.get(1).getContent())
                .isEqualTo("JUnit5 is a unit that framework and use extensively by Hygene.");
    }

    @Test
    void testParseNullCase() {
        articles = helpArticleLoader.parse("null.xml");
        assertThat(articles.size()).isEqualTo(0);
    }
}
