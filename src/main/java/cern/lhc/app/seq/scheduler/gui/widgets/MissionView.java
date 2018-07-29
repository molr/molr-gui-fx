/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static org.minifx.workbench.domain.PerspectivePos.CENTER;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMissionInstance;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import javafx.application.Platform;
import org.minifx.workbench.annotations.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMission;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

@View(at = CENTER, in = MissionsPerspective.class)
@Component
public abstract class MissionView extends BorderPane {

    private final TabPane tabPane;

    public MissionView(@Autowired ExecutableAdapter executableAdapter) {
        tabPane = new TabPane();
        setCenter(tabPane);
    }

    @EventListener
    public void update(ViewMission viewMission) {
        Platform.runLater(() -> {
            MissionPane seqPane = sequencePane(viewMission.missionDescription());
            addTab(viewMission.missionDescription().mission(), seqPane);
        });
    }


    @EventListener
    public void update(ViewMissionInstance viewMissionInstance) {
        Platform.runLater(() -> {
            MissionPane missionPane = missionPane(viewMissionInstance.missionDescription(), viewMissionInstance.missionHandle());
            addTab(viewMissionInstance.missionDescription().mission(), missionPane);
        });
    }

    private void addTab(Mission mission, MissionPane seqPane) {
        Tab tab = new Tab(mission.name(), seqPane);
        tabPane.getTabs().add(tab);
    }

    @Lookup
    public abstract MissionPane sequencePane(MissionDescription executable);

    @Lookup
    public abstract MissionPane missionPane(MissionDescription missionDescription, MissionHandle missionHandle);
}
