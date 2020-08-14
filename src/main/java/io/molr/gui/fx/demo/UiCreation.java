package io.molr.gui.fx.demo;

import com.google.common.collect.ImmutableList;
import io.molr.commons.domain.Mission;
import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.gui.fx.support.SimpleMissionControl;
import io.molr.mole.core.support.domain.VoidStub1;
import io.molr.mole.core.support.domain.VoidStub3;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;

import static io.molr.commons.domain.Placeholder.aString;
import static io.molr.commons.domain.Placeholder.anInteger;
import static io.molr.mole.core.support.MissionStubs.stub;
import static java.util.stream.Collectors.toList;

public final class UiCreation {

    /*
     * The following are examples of mission stubs: They are useful, in case you know in advance how the parameter
     * structure of the mission looks like and you want to use the mission from code. This way, the parameters will be
     * type checked at compile time ;-)
     */
    private static VoidStub1<String> CONTEXTUAL_MISSION = stub("contextual mission").withParameters(aString("deviceName"));
    private static VoidStub3<String, Integer, Integer> PARAMETRIZED_MISSION = stub("Executable Leafs Demo Mission (parametrized)") //
            .withParameters(aString("aMessage"), anInteger("iterations"), anInteger("sleepMillis"));

    private UiCreation() {
        /* only static helper methods */
    }

    public static Scene createScene(MolrFxSupport support) {
        Set<Mission> missions = support.executableMissions();

        System.out.println(missions);

        List<Button> debugButtons = missions.stream().map(m ->
                button("debug '" + m.name() + "'", () -> support.debug(m).inNewStage()))
                .collect(toList());

        List<Button> runButtons = missions.stream().map(m ->
                button("run '" + m.name() + "'", () -> support.debug(m).inNewStage().ifPresent(SimpleMissionControl::resume)))
                .collect(toList());

        List<Button> stubButtons = ImmutableList.of(//
                button("debug stub1", () -> support.debug(CONTEXTUAL_MISSION, "anyDevice").inNewStage()),//
                button("run stub1", () -> support.debug(CONTEXTUAL_MISSION, "anotherDevice").inNewStage().ifPresent(SimpleMissionControl::resume)),
                button("debug stub3", () -> support.debug(PARAMETRIZED_MISSION, "Hello World", 3, 200).inNewStage()),
                button("run stub3", () -> support.debug(PARAMETRIZED_MISSION, "Hello Molr", 6, 100).inNewStage().ifPresent(SimpleMissionControl::resume))
        );

        VBox root = new VBox();
        root.getChildren().addAll(debugButtons);
        root.getChildren().addAll(runButtons);
        root.getChildren().addAll(stubButtons);
        return new Scene(root);
    }

    private static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(e -> action.run());
        return button;
    }
}
