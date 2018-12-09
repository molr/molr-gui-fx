/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.widgets;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionParameterDescription;
import io.molr.gui.fx.commands.ViewMission;
import io.molr.gui.fx.commands.ViewMissionInstance;
import io.molr.gui.fx.perspectives.MissionsPerspective;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.minifx.workbench.annotations.View;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.minifx.workbench.domain.PerspectivePos.CENTER;

@View(at = CENTER, in = MissionsPerspective.class)
@Component
public abstract class MissionView extends BorderPane {

    private final TabPane tabPane;

    public MissionView() {
        tabPane = new TabPane();
        setCenter(tabPane);
       // addKeyboardShortcuts();

    }

    @EventListener
    public void update(ViewMission viewMission) {
        Platform.runLater(() -> {
            MissionPane seqPane = missionPane(viewMission.mission(), viewMission.description());
            addTab(viewMission.mission(), seqPane);
        });
    }

    private void addKeyboardShortcuts(){
        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F2){
                System.out.println("F2");
            }
            else
            {
                System.out.println(e.getCode() + " was pressed");
            }
        });
    }





    @EventListener
    public void update(ViewMissionInstance viewMissionInstance) {
        Platform.runLater(() -> {
            MissionPane missionPane = missionPane(viewMissionInstance.missionInstance().mission(), viewMissionInstance.description(), viewMissionInstance.missionInstance().handle());
            addTab(viewMissionInstance.missionInstance().mission(), missionPane);
        });
    }

    private void addTab(Mission mission, MissionPane seqPane) {
        Tab tab = new Tab(mission.name(), seqPane);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    @Lookup
    public abstract MissionPane missionPane(Mission mission, MissionParameterDescription description);

    @Lookup
    public abstract MissionPane missionPane(Mission mission, MissionParameterDescription description, MissionHandle missionHandle);
}
