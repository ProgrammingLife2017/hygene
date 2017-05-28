package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link BookmarkCreateController}.
 */
final class BookmarksTableControllerTest extends UITest {
    private BookmarkTableController bookmarkTableController;
    private BooleanProperty visibleProperty;


    @Override
    public void beforeEach() {
        final SimpleBookmarkStore simpleBookmarkStore = mock(SimpleBookmarkStore.class);
        visibleProperty = new SimpleBooleanProperty();
        when(simpleBookmarkStore.getTableVisibleProperty()).thenReturn(visibleProperty);

        bookmarkTableController = new BookmarkTableController();
        bookmarkTableController.setSimpleBookmarkStore(simpleBookmarkStore);
    }


    @Test
    void testHideAction() {
        final boolean originalVisibility = visibleProperty.get();

        interact(() -> bookmarkTableController.toggleVisibilityAction(mock(ActionEvent.class)));

        assertThat(originalVisibility).isNotEqualTo(visibleProperty.get());
    }
}
