package io.molr.gui.fx.perspectives;

import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.NoGutters;
import org.minifx.workbench.domain.Perspective;

import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.DRIBBBLE;

@Name("Debug")
@Icon(DRIBBBLE)
@NoGutters
public interface DebugPerspective extends Perspective {
}
