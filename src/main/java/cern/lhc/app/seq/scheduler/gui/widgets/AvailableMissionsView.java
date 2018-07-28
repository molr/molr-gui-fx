package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
        buttons.getChildren().add(debugButton);
        return buttons;
    }

    private void instantiateSelectedMission() {
        Mission mission = missionListView.getSelectionModel().getSelectedItem();
        molrService.instantiate(mission, ImmutableMap.of());
    }

    private ListView<Mission> newListView() {
        List<Mission> missions = molrService.executableMissions().collectList().block();
        ListView<Mission> list = new ListView<>(FXCollections.observableArrayList(missions));
        list.setCellFactory(nonNullItemText(Mission::name));
        return list;
    }

}
