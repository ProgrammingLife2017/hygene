package org.dnacronym.hygene.ui.help;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Help article loader
 */
public class HelpArticleLoader {
    public static final String HELP_MENU_FILE = "ui/help/articles.xml";
    private static final Logger LOGGER = LogManager.getLogger(HelpArticleLoader.class);

    public static Map<String, String> helpData;

    static Document parseXML() {
        Document document = null;
        try {
            File fXmlFile = new File(HELP_MENU_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(fXmlFile);
            document.normalizeDocument();
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error(e);
        }
        return document;
    }

    public static void loadHelpData() {
        Document data = parseXML();
        if (data != null) {
            NodeList articles = data.getElementsByTagName("articles");
            System.out.println();
        }

    }
}
