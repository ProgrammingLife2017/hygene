package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;

public interface ColorMode {
    Color calculateColor(final Node node);
}
