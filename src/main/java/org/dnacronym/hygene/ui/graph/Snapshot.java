package org.dnacronym.hygene.ui.graph;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.dialogue.InformationDialogue;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * Represents a snapshot.
 */
public final class Snapshot {
    private static final Logger LOGGER = LogManager.getLogger(Snapshot.class);
    private static final String FILE_FORMAT = "png";

    private final File destination;


    /**
     * Constructs and initializes a {@link Snapshot}.
     *
     * @param destination destination location of a snapshot
     */
    private Snapshot(final File destination) {
        this.destination = destination;
    }


    /**
     * Uses a GFA file to determine the destination and constructs a new {@link Snapshot}.
     *
     * @param gfaFile a {@link GfaFile} instance
     * @return a new {@link Snapshot}
     */
    public static Snapshot forGfaFile(final GfaFile gfaFile) {
        final File file = new File(gfaFile.getFileName());
        final String gfaFileNameWithoutExtension = FilenameUtils.removeExtension(file.getName());

        final String fileName = gfaFileNameWithoutExtension + "_snapshot_" + new Date().getTime() + "." + FILE_FORMAT;
        final File destination = new File(file.getParent(), fileName);

        return new Snapshot(destination);
    }


    /**
     * Takes a snapshot of a canvas and saves it to the destination.
     * <p>
     * After the screenshot is taken it shows a dialogue to the user indicating the location of the snapshot.
     *
     * @param canvas a JavaFX {@link Canvas} object
     * @return the destination of the screenshot
     */
    public String take(final Canvas canvas) {
        final WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        final WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), FILE_FORMAT, destination);
            new InformationDialogue(
                    "Snapshot taken",
                    "You can find your snapshot here: " + destination.getAbsolutePath()
            ).show();
        } catch (final IOException e) {
            LOGGER.error("Snapshot could not be taken.", e);
            new ErrorDialogue(e).show();
        }

        return destination.getAbsolutePath();
    }
}
