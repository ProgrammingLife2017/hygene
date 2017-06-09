package org.dnacronym.hygene.ui.query;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


/**
 *
 */
public class Query {
    private final BooleanProperty visibleProperty;


    /**
     * Creates instance of {@link Query}.
     */
    public Query() {
        this.visibleProperty = new SimpleBooleanProperty(true);
    }


    /**
     * Returns the {@link BooleanProperty} which decides the visibility of the query pane.
     *
     * @return the {@link BooleanProperty} which decides the visibility of the query pane
     */
    public BooleanProperty getVisibleProperty() {
        return visibleProperty;
    }
}
