/**
 * Classes that deal with displaying the graph to the user and user interaction with the graph.
 * <ul>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphController} deals with relaying the graph to
 * {@link org.dnacronym.hygene.ui.graph.GraphVisualizer} and user input.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator} translates the unscaled coordinates of nodes to
 * onscreen pixel coordinates.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphMovementCalculator} translates pixel coordinates of user interactions
 * to unscaled coordinates of nodes.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphSliderController} deals with user input in via the slider below the graph,
 * and makes sure the slider corresponds with the current {@link org.dnacronym.hygene.models.Graph}.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphStore} stores a reference to the current
 * {@link org.dnacronym.hygene.parser.GfaFile} in memory.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.GraphVisualizer} wraps a {@link javafx.scene.canvas.Canvas} and draws the
 * current {@link org.dnacronym.hygene.models.Graph} using the coordinates calculated by
 * {@link org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator}.
 * </li>
 * <li>
 * {@link org.dnacronym.hygene.ui.graph.RTree} allows easy finding of nodes, making it easy to find nodes when the user
 * clicks onscreen.
 * </li>
 * </ul>
 */
package org.dnacronym.hygene.ui.graph;
