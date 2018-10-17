package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.gui.commands.ViewMissionInstance;
import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import cern.lhc.app.seq.scheduler.util.FormattedButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import org.minifx.workbench.annotations.Icon;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.molr.commons.domain.AgencyState;
import org.molr.commons.domain.MissionInstance;
import org.molr.agency.core.Agency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static cern.lhc.app.seq.scheduler.util.CellFactories.nonNullItemText;
import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Instances")
@Icon(value= FontAwesomeIcon.SITEMAP, color="blue" )
@Order(2)
public class MissionInstancesView extends BorderPane {

    @Autowired
    private Agency agency;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<MissionInstance> listView;

    private final ObservableList<MissionInstance> activeMissions = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        agency.states().map(AgencyState::activeMissions).publishOn(FxSchedulers.fxThread()).subscribe(ms -> activeMissions.setAll(ms));

        listView = newListView();
        setCenter(listView);

        FlowPane buttons = new FlowPane();
        Button connectButton = new FormattedButton().getButton("Connect","Connect","Green");

        connectButton.setOnAction(event -> {
            MissionInstance instance = listView.getSelectionModel().getSelectedItem();
            agency.representationOf(instance.mission()).subscribe(r -> publisher.publishEvent(new ViewMissionInstance(instance, r)));
        });
        buttons.getChildren().add(connectButton);
        setBottom(buttons);


    }

    private void reloadListView(){
        init();
    }

    private ListView<MissionInstance> newListView() {
        ListView<MissionInstance> list = new ListView<>(activeMissions);
        list.setCellFactory(nonNullItemText(MissionInstance::toString));
        return list;
    }



}
