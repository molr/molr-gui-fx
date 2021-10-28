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
import io.molr.mole.core.api.Mole;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import org.minifx.workbench.annotations.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.minifx.workbench.domain.PerspectivePos.CENTER;

/**
 * Tab pane filled with tabs to setup and control mission instances
 */
@View(at = CENTER, in = MissionsPerspective.class)
@Component
public class MissionView extends BorderPane {

    @Autowired
    private Mole mole;

    private final TabPane tabPane;

    public MissionView() {
        tabPane = new TabPane();
        setCenter(tabPane);
        // addKeyboardShortcuts();

    }

    @EventListener
    public void update(ViewMission viewMission) {
        Platform.runLater(() -> {
            MissionInstanceSetupAndControlPane seqPane = missionPane(viewMission.mission(), viewMission.description());
            addTab(viewMission.mission(), seqPane);
        });
    }

    private void addKeyboardShortcuts() {
        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F2) {
                System.out.println("F2");
            } else {
                System.out.println(e.getCode() + " was pressed");
            }
        });
    }


    @EventListener
    public void update(ViewMissionInstance viewMissionInstance) {
        Platform.runLater(() -> {
        	MissionInstanceSetupAndControlPane missionPane = missionPane(viewMissionInstance.missionInstance().mission(), viewMissionInstance.description(), viewMissionInstance.missionInstance().handle());
            addTab(viewMissionInstance.missionInstance().mission(), missionPane);
        });
    }

    private void addTab(Mission mission, Pane seqPane) {
    	ScrollPane tabScrollView = new ScrollPane(seqPane);
    	tabScrollView.setFitToWidth(true);
        Tab tab = new Tab(mission.name(), tabScrollView);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }


    /*
     * Was missionPane
     */
    private MissionInstanceSetupAndControlPane missionPane(Mission mission, MissionParameterDescription description) {
        return new MissionInstanceSetupAndControlPane(mole, mission);
    }

    private MissionInstanceSetupAndControlPane missionPane(Mission mission, MissionParameterDescription description, MissionHandle missionHandle) {
        return new MissionInstanceSetupAndControlPane(mole, mission, missionHandle);
    }
}
