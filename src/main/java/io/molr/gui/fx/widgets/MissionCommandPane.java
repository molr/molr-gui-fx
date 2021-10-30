package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionCommand;
import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionState;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class MissionCommandPane extends TitledPane{

	private final String TITLE = "Mission Instance";
	
	private VBox box = new VBox();
	private FormattedButton disposeButton;
	
	public MissionCommandPane(Mole mole, MissionHandle handle) {
		super();
		
		this.setText(TITLE);
		this.setContent(box);
		
		box.setPadding(new Insets(10, 10, 10, 10));
        box.getChildren().setAll(new Label(handle.toString()));
        disposeButton = new FormattedButton("Dispose", "Instantiate", "Blue");
        disposeButton.getButton().setDisable(true);
        disposeButton.getButton().setOnAction(event -> {
            mole.instruct(handle, MissionCommand.DISPOSE);
        });
        box.getChildren().add(disposeButton.getButton());
		mole.statesFor(handle).publishOn(FxThreadScheduler.instance()).subscribe(this::onMissionStateUpdate);
		
	}
	
	public void onMissionStateUpdate(MissionState missionState) {
        boolean disableDisposeButton = !missionState.allowedMissionCommands().contains(MissionCommand.DISPOSE);
        disposeButton.getButton().setDisable(disableDisposeButton);
	}
	
}
