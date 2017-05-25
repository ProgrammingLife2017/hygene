package org.dnacronym.hygene.ui.help;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link HelpArticleParser}.
 */
class HelpArticleParserTest {
    private HelpArticleParser helpArticleLoader;


    @BeforeEach
    void setUp() {
        helpArticleLoader = new HelpArticleParser();
    }


    @Test
    void parseXML() {
        helpArticleLoader.loadXML(HelpArticleParser.DEFAULT_HELP_MENU_FILE);
    }

    @Test
    void loadHelpData() {
        helpArticleLoader.parse();
    }
}
