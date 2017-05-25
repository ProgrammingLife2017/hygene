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
 * Help article loader
 */
public class HelpArticleParser {
    public static final String DEFAULT_HELP_MENU_FILE = "src/main/resources/ui/help/articles.xml";
    private static final Logger LOGGER = LogManager.getLogger(HelpArticleParser.class);

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

    public List<HelpArticle> parse() {
        return parse(DEFAULT_HELP_MENU_FILE);
    }

    public List<HelpArticle> parse(final String filename) {
        Document document = loadXML(filename);

        System.out.println("Root element :" + document.getDocumentElement().getNodeName());

        Element articlesElement = (Element) document.getDocumentElement().getElementsByTagName("articles").item(0);
        List<HelpArticle> articles = parseArticles(articlesElement);

        return articles;
    }

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

    HelpArticle parseArticleElement(final Element article) {
        String title = article.getElementsByTagName("title").item(0).getTextContent();
        String content = article.getElementsByTagName("content").item(0).getTextContent();

        HelpArticle helpArticle = new HelpArticle(title, content);
        return helpArticle;
    }
}
