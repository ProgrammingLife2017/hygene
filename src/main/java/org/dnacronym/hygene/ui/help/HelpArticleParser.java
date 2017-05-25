package org.dnacronym.hygene.ui.help;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses {@link HelpArticle}s.
 */
public final class HelpArticleParser {
    static final String DEFAULT_HELP_MENU_FILE = "src/main/resources/ui/help/articles.xml";
    private static final Logger LOGGER = LogManager.getLogger(HelpArticleParser.class);

    /**
     * Load and XML document.
     *
     * @param filename the file name
     * @return the {@link Document}
     */
    Document loadXML(final String filename) {
        Document document = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(filename);
            document.normalizeDocument();
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error(e);
        }
        return document;
    }

    /**
     * Will call {@link HelpArticleParser#parse(String)} with default location for help articles.
     *
     * @return A {@link List} of {@link HelpArticle}
     */
    public List<HelpArticle> parse() {
        return parse(DEFAULT_HELP_MENU_FILE);
    }

    /**
     * Will load the provided XML file and parse its contents into {@link HelpArticle}s.
     *
     * @param filename the filename
     * @return A {@link List} of {@link HelpArticle}
     */
    public List<HelpArticle> parse(final String filename) {
        Document document = loadXML(filename);

        if (document != null) {
            Element articlesElement = (Element) document.getDocumentElement().getElementsByTagName("articles").item(0);
            return parseArticles(articlesElement);
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * Will parse the articles {@link Element} node in the help articles XML document.
     *
     * @param articlesElement the {@link Element} node
     * @return A {@link List} of {@link HelpArticle}
     */
    List<HelpArticle> parseArticles(final Element articlesElement) {
        List<HelpArticle> articles = new ArrayList<>();

        NodeList article = articlesElement.getElementsByTagName("article");

        for (int i = 0; i < article.getLength(); i++) {
            Node articleNode = article.item(i);

            if (articleNode.getNodeType() == Node.ELEMENT_NODE) {
                Element articleElement = (Element) articleNode;

                HelpArticle helpArticle = parseArticleElement(articleElement);

                articles.add(helpArticle);
            }
        }

        return articles;
    }

    /**
     * Parses {@link Element} node into an {@link HelpArticle}.
     *
     * @param article the article {@link Element} node
     * @return the {@link HelpArticle}
     */
    HelpArticle parseArticleElement(final Element article) {
        String title = article.getElementsByTagName("title").item(0).getTextContent();
        String content = article.getElementsByTagName("content").item(0).getTextContent();

        return new HelpArticle(title, content);
    }
}
