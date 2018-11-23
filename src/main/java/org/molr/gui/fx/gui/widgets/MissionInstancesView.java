package org.molr.gui.fx.gui.widgets;

import org.molr.gui.fx.gui.commands.ViewMissionInstance;
import org.molr.gui.fx.gui.perspectives.MissionsPerspective;
import org.molr.gui.fx.util.FormattedButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import org.minifx.workbench.annotations.Icon;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.molr.commons.domain.AgencyState;
import org.molr.commons.domain.MissionInstance;
import org.molr.agency.core.Agency;
import org.molr.gui.fx.util.CellFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
        FormattedButton connectButton = new FormattedButton("Connect","Connect","Green");

        connectButton.getButton().setOnAction(event -> {
            MissionInstance instance = listView.getSelectionModel().getSelectedItem();
            agency.parameterDescriptionOf(instance.mission())
                    .map(d -> new ViewMissionInstance(instance, d))
                    .subscribe(publisher::publishEvent);
        });
        buttons.getChildren().add(connectButton.getButton());
        setBottom(buttons);


    }

    private void reloadListView(){
        init();
    }

    private ListView<MissionInstance> newListView() {
        ListView<MissionInstance> list = new ListView<>(activeMissions);
        list.setCellFactory(CellFactories.nonNullItemText(MissionInstance::toString));
        return list;
    }



}
