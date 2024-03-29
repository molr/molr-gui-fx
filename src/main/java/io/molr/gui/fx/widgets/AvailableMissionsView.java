package io.molr.gui.fx.widgets;

import static org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS_CIRCLE;
import io.molr.commons.domain.*;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.commands.ViewMission;
import io.molr.gui.fx.commands.ViewMissionInstance;
import io.molr.gui.fx.perspectives.MissionsPerspective;
import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Icon;
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
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@Order(1)
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Available")
@Icon(value = PLUS_CIRCLE, color = "green")
public class AvailableMissionsView extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailableMissionsView.class);

    @Autowired
    private Mole mole;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<Mission> missionListView;
    private ObservableList<Mission> missions = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        this.missionListView = newListView();
        setCenter(missionListView);
        setBottom(buttonsPane());
        mole.states().publishOn(FxThreadScheduler.instance()).subscribe(this::update);

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

        FormattedButton showButton = new FormattedButton("Show", "Show", "Green");
        showButton.getButton().setOnAction(event -> showSelectedMission());

        FormattedButton instantiateButton = new FormattedButton("Instantiate", "Instantiate", "Blue");
        instantiateButton.getButton().setOnAction(event -> instantiateSelectedMission());

        FormattedButton debugButton = new FormattedButton("Debug", "Debug", "Cyan");
        debugButton.getButton().setOnAction(event -> debugMission());

        buttons.getChildren().addAll(showButton.getButton(), instantiateButton.getButton(), debugButton.getButton());
        return buttons;
    }

    private void debugMission() {
        Mission mission = selectedMission();
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return;
        }

        instantiate(mission)
                .map(h -> new MissionInstance(h, mission))
                .zipWith(mole.parameterDescriptionOf(mission))
                .map(tuple22 -> new ViewMissionInstance(tuple22.getT1(), tuple22.getT2()))
                .subscribe(publisher::publishEvent);
    }

    private void showSelectedMission() {
        Mission mission = selectedMission();
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return;
        }

        mole.parameterDescriptionOf(mission)
                .map(d -> new ViewMission(mission, d))
                .subscribe(publisher::publishEvent);
    }

    private Mono<MissionHandle> instantiateSelectedMission() {
        return instantiate(selectedMission());
    }

    private Mono<MissionHandle> instantiate(Mission mission) {
        if (mission == null) {
            LOGGER.warn("No mission selected. Doing nothing");
            return Mono.empty();
        }

        Optional<Map<String, Object>> parameters = parametersFor(mission);
        if (!parameters.isPresent()) {
            LOGGER.info("Aborted by user. Mission will not be instantiated.");
            return Mono.empty();
        }
        Map<String, Object> params = parameters.get();
        return mole.instantiate(mission, params);
    }

    private Optional<Map<String, Object>> parametersFor(Mission mission) {
        MissionParameterDescription description = mole.parameterDescriptionOf(mission).block();

        if (description.parameters().isEmpty()) {
            /* This is a valid situation! There are no parameters, so no need to query them ;-)*/
            return Optional.of(emptyMap());
        }

        Dialog<Map<String, Object>> dialog = MolrDialogs.parameterDialogFor(mission, description);
        return dialog.showAndWait();
    }

    private Mission selectedMission() {
        return missionListView.getSelectionModel().getSelectedItem();
    }

    private ListView<Mission> newListView() {
        ListView<Mission> list = new ListView<>(this.missions);
        list.setCellFactory(CellFactories.nonNullItemText(Mission::name));
        return list;
    }

}
