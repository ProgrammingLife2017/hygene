package org.dnacronym.hygene.ui.console;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.dnacronym.hygene.core.HygeneEventBus;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;


/**
 * Custom Appender plugin for Log4j2 that will cause the logger output appended to a JavaFX TextArea in the GUI.
 */
@Plugin(name = "JFXAppender", category = "Core", elementType = "appender", printObject = true)
public final class JFXAppender extends AbstractAppender {
    /**
     * Constructor for creating a new JFXAppender.
     *
     * @param name             the Appender's name
     * @param filter           the {@link Filter} to associate with the Appender
     * @param layout           the layout to use to format the event
     * @param ignoreExceptions if true, exceptions will be logged and suppressed. If false errors will be logged and
     *                         then passed to the application
     */
    protected JFXAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
                          final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);

        HygeneEventBus.getInstance().register(this);
    }


    /**
     * Method for initializing a new instance of JFXAppender; used by Log4j.
     *
     * @param layout the layout for events
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
    @SuppressWarnings({
            "PMD.AvoidCatchingGenericException", // Exception class is unknown
            "PMD.EmptyCatchBlock", // Exception can neither be logged nor be rethrown
            "squid:S1166" // Exception cannot be logged or rethrown
    })
    public void append(final LogEvent event) {
        try {
            HygeneEventBus.getInstance().post(event);
        } catch (final RuntimeException e) {
            // We can't actually log the exception here since that would cause the same problem
        }
    }
}
