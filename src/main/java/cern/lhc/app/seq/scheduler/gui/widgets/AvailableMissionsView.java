package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMission;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMissionInstance;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import com.google.common.collect.ImmutableMap;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;

import static cern.lhc.app.seq.scheduler.util.CellFactories.nonNullItemText;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@Order(1)
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Available")
public class AvailableMissionsView extends BorderPane {

    @Autowired
    private MolrService molrService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<Mission> missionListView;

    @PostConstruct
    public void init() {
        this.missionListView = newListView();
        setCenter(missionListView);

        setBottom(buttonsPane());
    }

    private FlowPane buttonsPane() {
        FlowPane buttons = new FlowPane();
        Button showButton = new Button("show");
        showButton.setOnAction(event -> showMission());

        Button instantiateButton = new Button("instantiate");
        instantiateButton.setOnAction(event -> instantiateSelectedMission());

        Button debugButton = new Button("debug");
        debugButton.setOnAction(event -> debugMission());

        buttons.getChildren().addAll(showButton, instantiateButton, debugButton);
        return buttons;
    }

    private void debugMission() {
        Mission mission = selectedMission();
        instantiate(mission).subscribe(h -> {
            molrService.representationOf(mission).subscribe(r -> publisher.publishEvent(new ViewMissionInstance(h, r)));
        });
    }

    private void showMission() {
        Mono<MissionDescription> representation = molrService.representationOf(selectedMission());
        representation.subscribe(r -> publisher.publishEvent(new ViewMission(r)));
    }

    private Mono<MissionHandle> instantiateSelectedMission() {
        return instantiate(selectedMission());
    }

    private Mono<MissionHandle> instantiate(Mission mission) {
        return molrService.instantiate(mission, ImmutableMap.of());
    }

    private Mission selectedMission() {
        return missionListView.getSelectionModel().getSelectedItem();
    }

    private ListView<Mission> newListView() {
        List<Mission> missions = molrService.executableMissions().collectList().block();
        ListView<Mission> list = new ListView<>(FXCollections.observableArrayList(missions));
        list.setCellFactory(nonNullItemText(Mission::name));
        return list;
    }

}