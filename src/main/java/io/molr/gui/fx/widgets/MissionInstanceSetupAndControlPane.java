package io.molr.gui.fx.widgets;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionHandle;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.mole.core.api.Mole;
import javafx.scene.layout.BorderPane;

public class MissionInstanceSetupAndControlPane extends BorderPane{
	
	public MissionInstanceSetupAndControlPane(Mole mole, Mission mission) {
		MissionPane missionPane = new MissionPane(mole, mission);
		this.setCenter(missionPane);
		missionPane.missionHandle().publishOn(FxThreadScheduler.instance()).subscribe(missionHandle->{
			addStrandCommandPane(mole, missionHandle);
		});
	}

	public MissionInstanceSetupAndControlPane(Mole mole, Mission mission, MissionHandle missionHandle) {
		this.setCenter(new MissionPane(mole, mission, missionHandle));
		addStrandCommandPane(mole, missionHandle);
	}
	
	private void addStrandCommandPane(Mole mole, MissionHandle missionHandle) {
		StrandCommandPane commandPane = new StrandCommandPane(mole, missionHandle);
		this.setRight(commandPane);
	}

}
