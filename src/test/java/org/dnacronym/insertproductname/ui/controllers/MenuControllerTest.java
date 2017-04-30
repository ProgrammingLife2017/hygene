package org.dnacronym.insertproductname.ui.controllers;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;


/**
 * Unit test for {@link MenuController}.
 */
class MenuControllerTest {
    private MenuController menuController;


    @BeforeEach
    void beforeEach() {
        menuController = new MenuController(mock(Stage.class));
    }


    @Test
    public void testOpenAction() {
        menuController.openAction(mock(ActionEvent.class));

        // TODO check something
    }
}
