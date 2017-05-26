/**
 * Deals with displaying the console window as view in the GUI. Uses {@link org.dnacronym.hygene.ui.console.JFXAppender}
 * to keep track of new log messages and notify {@link org.dnacronym.hygene.ui.console.ConsoleController} through
 * a JavaFX {@link javafx.beans.property.StringProperty}.
 * <ul>
 * <li>
 * {@link org.dnacronym.hygene.ui.console.ConsoleController} is the controller which deals with user input and
 * relays console messages to the view.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.console.ConsoleMessage} is a wrapper class of console messages.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.console.ConsoleWrapper} deals with creating a new console.
 * </li>
 * </ul>
 */
package org.dnacronym.hygene.ui.console;
