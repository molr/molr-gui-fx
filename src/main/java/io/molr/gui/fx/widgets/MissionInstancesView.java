package io.molr.gui.fx.widgets;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import io.molr.commons.domain.AgencyState;
import io.molr.commons.domain.MissionInstance;
import io.molr.gui.fx.commands.ViewMissionInstance;
import io.molr.gui.fx.perspectives.MissionsPerspective;
import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
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
    private Mole mole;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<MissionInstance> listView;

    private final ObservableList<MissionInstance> activeMissions = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        mole.states().map(AgencyState::activeMissions).publishOn(FxSchedulers.fxThread()).subscribe(ms -> activeMissions.setAll(ms));

        listView = newListView();
        setCenter(listView);

        FlowPane buttons = new FlowPane();
        FormattedButton connectButton = new FormattedButton("Connect","Connect","Green");

        connectButton.getButton().setOnAction(event -> {
            MissionInstance instance = listView.getSelectionModel().getSelectedItem();
            mole.parameterDescriptionOf(instance.mission())
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
