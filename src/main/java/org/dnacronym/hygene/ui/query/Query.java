package org.dnacronym.hygene.ui.query;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dnacronym.hygene.graph.NewNode;


/**
 * Queries are performed here.
 */
public class Query {
    private final BooleanProperty visibleProperty;

    private final ObservableList<NewNode> queriedNodes;


    /**
     * Creates instance of {@link Query}.
     */
    public Query() {
        this.visibleProperty = new SimpleBooleanProperty(true);
        queriedNodes = FXCollections.observableArrayList();
    }


    /**
     * Performs a query by looking at the sequences of nodes and returning the nodes which contain the passed sequence.
     *
     * @param sequence the sequence to search for inside the sequences of nodes
     */
    public void query(final String sequence) {

    }

    /**
     * Returns the {@link BooleanProperty} which decides the visibility of the query pane.
     *
     * @return the {@link BooleanProperty} which decides the visibility of the query pane
     */
    public BooleanProperty getVisibleProperty() {
        return visibleProperty;
    }

    /**
     * Returns the {@link ObservableList} of the most recently queried nodes.
     *
     * @return the {@link ObservableList} of the most recently queried nodes
     */
    public ObservableList<NewNode> getQueriedNodes() {
        return queriedNodes;
    }
}
