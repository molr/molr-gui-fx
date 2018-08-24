package cern.lhc.app.seq.scheduler.gui.widgets;

import javafx.collections.ObservableList;
import org.molr.commons.api.domain.*;
import org.molr.commons.api.service.Agency;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

import static cern.lhc.app.seq.scheduler.util.CellFactories.nonNullItemText;
import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@Order(1)
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Available")
public class AvailableMissionsView extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailableMissionsView.class);

    @Autowired
    private Agency agency;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<Mission> missionListView;
    private ObservableList<Mission> missions = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        this.missionListView = newListView();
        setCenter(missionListView);
        setBottom(buttonsPane());
        agency.states().publishOn(fxThread()).subscribe(this::update);
    }

    private void update(AgencyState state) {
        List<Mission> missionList = state.executableMissions().stream().sorted(comparing(Mission::name)).collect(toList());
        this.missions.setAll(missionList);
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
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return;
        }
        instantiate(mission).subscribe(h -> {
            agency.representationOf(mission).subscribe(r -> publisher.publishEvent(new ViewMissionInstance(new MissionInstance(h, mission), r)));
        });
    }

    private void showMission() {
        Mission mission = selectedMission();
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return;
        }
        Mono<MissionRepresentation> representation = agency.representationOf(mission);
        representation.subscribe(r -> publisher.publishEvent(new ViewMission(mission, r)));
    }

    private Mono<MissionHandle> instantiateSelectedMission() {
        return instantiate(selectedMission());
    }

    private Mono<MissionHandle> instantiate(Mission mission) {
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return Mono.empty();
        }
        return agency.instantiate(mission, ImmutableMap.of());
    }

    private Mission selectedMission() {
        return missionListView.getSelectionModel().getSelectedItem();
    }

    private ListView<Mission> newListView() {

        ListView<Mission> list = new ListView<>(this.missions);
        list.setCellFactory(nonNullItemText(Mission::name));
        return list;
    }

}
