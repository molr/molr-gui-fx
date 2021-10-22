package io.molr.gui.fx.perspectives;

import static org.controlsfx.glyphfont.FontAwesome.Glyph.USER_SECRET;
import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.NoGutters;
import org.minifx.workbench.domain.Perspective;
import org.springframework.core.annotation.Order;

@Order(1)
@Name("Missions")
@Icon(value = USER_SECRET, color = "blue")
@NoGutters
public interface MissionsPerspective extends Perspective {
}
