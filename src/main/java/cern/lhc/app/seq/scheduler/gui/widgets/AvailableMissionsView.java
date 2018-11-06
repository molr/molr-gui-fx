package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.gui.commands.ViewMission;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMissionInstance;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import cern.lhc.app.seq.scheduler.util.FormattedButton;
import com.google.common.collect.ImmutableMap;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.molr.commons.domain.AgencyState;
import org.molr.commons.domain.Mission;
import org.molr.commons.domain.MissionHandle;
import org.molr.commons.domain.MissionInstance;
import org.molr.commons.domain.MissionRepresentation;
import org.molr.agency.core.Agency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import java.util.List;

import static cern.lhc.app.seq.scheduler.util.CellFactories.nonNullItemText;
import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@Order(1)
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Available")
@Icon (value= FontAwesomeIcon.PLUS_CIRCLE, color="green" )
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

        missionListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    //Mission mission = selectedMission();
                    instantiateSelectedMission();
                }
            }
        });


    }

    private void update(AgencyState state) {
        List<Mission> missionList = state.executableMissions().stream().sorted(comparing(Mission::name)).collect(toList());
        Mission previousSelected = missionListView.getSelectionModel().getSelectedItem();
        this.missions.setAll(missionList);
        if ((previousSelected != null) && missions.contains(previousSelected)) {
            missionListView.getSelectionModel().select(previousSelected);
        } else if (!missionList.isEmpty()) {
            missionListView.getSelectionModel().select(0);
        }
    }

    private FlowPane buttonsPane() {
        FlowPane buttons = new FlowPane();

        Button showButton = new FormattedButton().getButton("Show","Show","Green");
        showButton.setOnAction(event -> showSelectedMission());

        Button instantiateButton = new FormattedButton().getButton("Instantiate","Instantiate","Blue");
        instantiateButton.setOnAction(event -> instantiateSelectedMission());

        Button debugButton = new FormattedButton().getButton("Debug","Debug","Cyan");
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

    private void showSelectedMission() {
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
