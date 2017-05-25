package org.dnacronym.hygene.ui.help;

/**
 * This class contains all data required for a displaying a help-article in hte the GUI.
 */
public class HelpArticle {
    protected String title;
    protected String content;

    /**
     * Constructs a new HelpArticle.
     *
     * @param title   the title of the article
     * @param content the content nof the article
     */
    public HelpArticle(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HelpArticle{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
