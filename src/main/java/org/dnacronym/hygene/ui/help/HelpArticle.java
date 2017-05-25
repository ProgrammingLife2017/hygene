package org.dnacronym.hygene.ui.help;

/**
 * This class contains all data required for a displaying a help-article in hte the GUI.
 */
public final class HelpArticle {
    /**
     * The Title.
     */
    private String title;
    /**
     * The Content.
     */
    private String content;

    /**
     * Constructs a new HelpArticle.
     *
     * @param title   the title of the article
     * @param content the content nof the article
     */
    HelpArticle(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Gets the article's title.
     *
     * @return the title
     */
    String getTitle() {
        return title;
    }

    /**
     * Sets the article's title.
     *
     * @param title the title
     */
    public void setTitle(final String title) {
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
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HelpArticle{"
                + "title='" + title + '\''
                + ", content='" + content + '\'' + '}';
    }
}
