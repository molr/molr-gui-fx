package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.domain.molr.AgencyState;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.domain.molr.MissionInstance;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import cern.lhc.app.seq.scheduler.gui.commands.ViewMissionInstance;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import static cern.lhc.app.seq.scheduler.util.CellFactories.nonNullItemText;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Instances")
@Order(2)
public class MissionInstancesView extends BorderPane {

    @Autowired
    private MolrService molrService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<MissionInstance> listView;

    private final ObservableList<MissionInstance> activeMissions = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        molrService.states().map(AgencyState::activeMissions).publishOn(FxSchedulers.fxThread()).subscribe(ms -> activeMissions.setAll(ms));

        listView = newListView();
        setCenter(listView);

        FlowPane buttons = new FlowPane();
        Button connectButton = new Button("connect");
        connectButton.setOnAction(event -> {
            MissionInstance instance = listView.getSelectionModel().getSelectedItem();
            molrService.representationOf(instance.mission()).subscribe(r -> publisher.publishEvent(new ViewMissionInstance(instance.handle(), r)));
        });
        buttons.getChildren().add(connectButton);
        setBottom(buttons);
    }

    private ListView<MissionInstance> newListView() {
        ListView<MissionInstance> list = new ListView<>(activeMissions);
        list.setCellFactory(nonNullItemText(MissionInstance::toString));
        return list;
    }

}
