package io.molr.gui.fx.widgets;

import static org.controlsfx.glyphfont.FontAwesome.Glyph.SITEMAP;
import io.molr.commons.domain.AgencyState;
import io.molr.commons.domain.MissionCommand;
import io.molr.commons.domain.MissionInstance;
import io.molr.commons.domain.MissionState;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.commands.ViewMissionInstance;
import io.molr.gui.fx.perspectives.MissionsPerspective;
import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import org.minifx.workbench.annotations.Icon;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.minifx.workbench.domain.PerspectivePos.LEFT;

@Component
@View(at = LEFT, in = MissionsPerspective.class)
@Name("Instances")
@Icon(value = SITEMAP, color = "blue")
@Order(2)
public class MissionInstancesView extends BorderPane {

    private final static Logger LOGGER = LoggerFactory.getLogger(MissionInstancesView.class);

    @Autowired
    private Mole mole;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ListView<MissionInstance> listView;

    private final ObservableList<MissionInstance> activeMissions = FXCollections.observableArrayList();

    private FormattedButton disposeButton;

    private Disposable selectedMissionInstanceStatesSubscription;

    @PostConstruct
    public void init() {
        mole.states().map(AgencyState::activeMissions).publishOn(FxThreadScheduler.instance()).subscribe(ms -> activeMissions.setAll(ms));

        listView = newListView();
        setCenter(listView);

        FlowPane buttons = new FlowPane();
        FormattedButton connectButton = new FormattedButton("Connect", "Connect", "Green");

        connectButton.getButton().setOnAction(event -> {
            MissionInstance instance = listView.getSelectionModel().getSelectedItem();
            mole.parameterDescriptionOf(instance.mission())
                    .map(d -> new ViewMissionInstance(instance, d))
                    .subscribe(publisher::publishEvent);
        });
        buttons.getChildren().add(connectButton.getButton());

        disposeButton = new FormattedButton("Dispose", "Dispose", "Green");
        disposeButton.getButton().setDisable(true);
        disposeButton.getButton().setOnAction(event -> {
            MissionInstance selectedInstance = listView.getSelectionModel().getSelectedItem();
            mole.instruct(selectedInstance.handle(), MissionCommand.DISPOSE);
        });
        buttons.getChildren().add(disposeButton.getButton());

        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<MissionInstance>) c -> {
            disposeButton.getButton().setDisable(true);
            if (c.getList().size() == 1) {
                MissionInstance selectedInstance = c.getList().get(0);
                onSelectedMissionInstanceChange(selectedInstance);
            }
        });

        setBottom(buttons);
    }

    private void onSelectedMissionInstanceChange(MissionInstance selectedInstance) {
        if (selectedMissionInstanceStatesSubscription != null) {
            selectedMissionInstanceStatesSubscription.dispose();
        }
        Flux<MissionState> selectedMissionInstanceStatesFlux = mole.statesFor(selectedInstance.handle()).publishOn(FxThreadScheduler.instance());
        selectedMissionInstanceStatesSubscription = selectedMissionInstanceStatesFlux.subscribe(this::onSelectedMissionStateUpdate);
    }

    private void onSelectedMissionStateUpdate(MissionState missionState) {
        boolean disableDisposeButton = !missionState.allowedMissionCommands().contains(MissionCommand.DISPOSE);
        disposeButton.getButton().setDisable(disableDisposeButton);
        LOGGER.info((disableDisposeButton ? "disable" : "enable") + " dispose button on mission state update");
    }

    private ListView<MissionInstance> newListView() {
        ListView<MissionInstance> list = new ListView<>(activeMissions);
        list.setCellFactory(CellFactories.nonNullItemText(MissionInstance::toString));
        return list;
    }


}
