package org.dnacronym.hygene.ui.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;


/**
 * Custom Appender plugin for Log4j2 that will cause the logger output appended to a JavaFX {@code TextArea} in the GUI.
 */
@Plugin(name = "JFXAppender", category = "Core", elementType = "appender", printObject = true)
public final class JFXAppender extends AbstractAppender {
    private static Logger logger = LogManager.getLogger(JFXAppender.class.getSimpleName());

    private static volatile StringProperty consoleBinding = new SimpleStringProperty();

    /**
     * Constructor for creating a new JFXAppender.
     *
     * @param name             the Appender's name
     * @param filter           the {@code Filter} to associate with the Appender
     * @param layout           the layout to use to format the event
     * @param ignoreExceptions if true, exceptions will be logged and suppressed. If false errors will be logged and
     *                         then passed to the application
     */
    protected JFXAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
                          final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    /**
     * Getter for the console binding.
     *
     * @return the console binding.
     */
    public static StringProperty getConsoleBinding() {
        return consoleBinding;
    }

    /**
     * Method for initializing a new instance of JFXAppender; used by Log4j.
     *
     * @param layout the layout for events.
     * @param filter the initial filter of the appender
     * @return the newly created JFXAppender
     */
    @PluginFactory
    public static JFXAppender createAppender(
            @PluginElement("Layout") final Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter) {
        return new JFXAppender("JFXAppender", filter,
                layout == null ? PatternLayout.createDefaultLayout() : layout, true);
    }

    @Override
    public void append(final LogEvent event) {
        if (consoleBinding != null) {
            try {
                consoleBinding.setValue(new String(getLayout().toByteArray(event), "UTF-8") + "\n");
            } catch (UnsupportedEncodingException e) {
                logger.error(e);
            }
        }
    }
}
