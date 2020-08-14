package io.molr.gui.fx.demo;

import io.molr.commons.domain.Mission;
import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.gui.fx.support.SimpleMissionControl;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public final class UiCreation {

    private UiCreation() {
        /* only static helper methods */
    }

    public static Scene createScene(MolrFxSupport support) {
        Set<Mission> missions = support.executableMissions();
        List<Button> debugButtons = missions.stream().map(m -> {
            Button button = new Button("debug '" + m.name() + "'");
            button.setOnAction(e -> support.debug(m).inNewStage());
            return button;
        }).collect(toList());

        List<Button> runButtons = missions.stream().map(m -> {
            Button button = new Button("run '" + m.name() + "'");
            button.setOnAction(e -> support.debug(m).inNewStage().ifPresent(SimpleMissionControl::resume));
            return button;
        }).collect(toList());


        VBox root = new VBox();
        root.getChildren().addAll(debugButtons);
        root.getChildren().addAll(runButtons);
        return new Scene(root);
    }
}
