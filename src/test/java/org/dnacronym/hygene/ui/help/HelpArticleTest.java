package org.dnacronym.hygene.ui.help;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Units tests for {@link HelpArticleTest}.
 */
class HelpArticleTest {
    private HelpArticle helpArticle;


    @BeforeEach
    void setUp() {
        helpArticle = new HelpArticle("Lorum Ipsum", "Ipsum Lorum");
    }

    @Test
    void testGetTitle() {
        assertThat(helpArticle.getTitle()).isEqualTo("Lorum Ipsum");
    }

    @Test
    void testSetTitle() {
        helpArticle.setTitle("Lorum Lorum Ipsum");
        assertThat(helpArticle.getTitle()).isEqualTo("Lorum Lorum Ipsum");
    }

    @Test
    void testGetContent() {
        assertThat(helpArticle.getContent()).isEqualTo("Ipsum Lorum");
    }

    @Test
    void testSetContent() {
        helpArticle.setContent("Ipsum Ipsum Lorum");
        assertThat(helpArticle.getContent()).isEqualTo("Ipsum Ipsum Lorum");
    }

    @Test
    void testToString() {
        assertThat(helpArticle.toString()).isEqualTo("HelpArticle{title='Lorum Ipsum', content='Ipsum Lorum'}");
    }
}
