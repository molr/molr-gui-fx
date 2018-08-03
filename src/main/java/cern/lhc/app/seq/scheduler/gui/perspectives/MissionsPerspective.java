package cern.lhc.app.seq.scheduler.gui.perspectives;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.domain.Perspective;
import org.springframework.core.annotation.Order;

@Order(1)
@Name("Missions")
@Icon(value = FontAwesomeIcon.COMMENT, color = "lightblue")
public interface MissionsPerspective extends Perspective {
}