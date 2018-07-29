package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import cern.lhc.app.seq.scheduler.util.CellFactories;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
        Button debugButton = new Button("debug");
        debugButton.setOnAction(event -> instantiateSelectedMission());

        Button showButton = new Button("show");
        showButton.setOnAction(event -> showMission());

        buttons.getChildren().addAll(debugButton, showButton);
        return buttons;
    }

    private void showMission() {
        Mono<MissionDescription> representation = molrService.representationOf(selectedMission());
        representation.subscribe(r -> publisher.publishEvent(new Open(r.rootBlock())));
    }

    private void instantiateSelectedMission() {
        molrService.instantiate(selectedMission(), ImmutableMap.of());
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
