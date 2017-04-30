package org.dnacronym.insertproductname.ui.controllers;

import javafx.event.ActionEvent;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

/**
 * Unit test for {@link MenuController}.
 */
public class MenuControllerTest {
    private MenuController menuController;

    @BeforeEach
    void beforeEach() {
        menuController = new MenuController();
    }


    @Test
    public void testOpenAction() {
        menuController.openAction(mock(ActionEvent.class));

        // TODO check something
    }
}
