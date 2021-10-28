package io.molr.gui.fx.widgets;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.MissionState;
import io.molr.commons.domain.RunState;
import io.molr.commons.domain.Strand;
import io.molr.commons.domain.StrandCommand;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.mole.core.api.Mole;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class StrandCommandPane extends BorderPane{
    private TreeTableView<StrandLine> strandTableView;
    
    private List<EventHandler<KeyEvent>> eventsList = new ArrayList<EventHandler<KeyEvent>>();
    
    Mole mole;
    MissionHandle missionHandle;
    private final SimpleObjectProperty<MissionState> lastState = new SimpleObjectProperty<>();
    private final Map<StrandCommand, FormattedButton> commandButtons = new EnumMap<StrandCommand, FormattedButton>(StrandCommand.class);
    
    StrandCommandPane(Mole mole, MissionHandle handle){
    	this.mole = mole;
    	this.missionHandle = handle;
    	
    	mole.statesFor(handle).publishOn(FxThreadScheduler.instance()).subscribe(nextState->{
    		updateStates(nextState);
    	});

    	this.setCenter(createStrandCommandPane());
    }
    
    private void updateStates(MissionState missionState){
        
        this.lastState.set(missionState);
        Strand lastSelectedStrandNode = selectedStrand();

        TreeItem<StrandLine> rootItem = strandsTreeItemsFor(missionState);
        strandTableView.setRoot(rootItem);

        TreeItem<StrandLine> toBeSelected = find(rootItem, lastSelectedStrandNode);

        if (toBeSelected != null) {
            strandTableView.getSelectionModel().select(toBeSelected);
        } else {
            strandTableView.getSelectionModel().select(rootItem);
        }

        updateButtonStates();
    }
    
    private FormattedButton commandButton(StrandCommand command) {
        FormattedButton button = new FormattedButton(command.toString());
        button.getButton().setPrefWidth(200);
        button.getButton().setMnemonicParsing(false);
        button.getButton().setOnAction(event -> {
            Strand strand = selectedStrand();
            mole.instruct(missionHandle, strand, command);
        });
        return button;
    }

    private TreeItem<StrandLine> find(TreeItem<StrandLine> item, Strand strandToFind) {
        if (strandToFind == null) {
            return null;
        }
        if (strandToFind.equals(item.getValue().strand())) {
            return item;
        }
        for (TreeItem<StrandLine> child : item.getChildren()) {
            TreeItem<StrandLine> found = find(child, strandToFind);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    Strand selectedStrand() {
        return Optional.ofNullable(selectedStrandNode())
                .map(TreeItem::getValue)
                .map(StrandLine::strand)
                .orElse(null);
    }

    private TreeItem<StrandLine> selectedStrandNode() {
        return strandTableView.getSelectionModel().getSelectedItem();
    }

    private TreeItem<StrandLine> strandsTreeItemsFor(MissionState state) {
        Strand rootStrand = state.rootStrand();
        TreeItem<StrandLine> strandTreeItem = treeItemFor(rootStrand, state);
        strandTreeItem.setExpanded(true);
        return strandTreeItem;
    }

    private TreeItem<StrandLine> treeItemFor(Strand parent, MissionState state) {
        TreeItem<StrandLine> item = new TreeItem<>(new StrandLine(parent, state.runStateOf(parent)));
        Set<TreeItem<StrandLine>> childNodes = state.childrenOf(parent).stream().map(s -> treeItemFor(s, state)).collect(toSet());
        item.getChildren().addAll(childNodes);
        return item;
    }

    private Pane createStrandCommandPane() {
        strandTableView = new TreeTableView<>();

        TreeTableColumn<StrandLine, String> idColumn = new TreeTableColumn<>("Strand");
        idColumn.setCellValueFactory(MissionPane.nullSafe(StrandLine::idProperty));

        TreeTableColumn<StrandLine, RunState> runStateCollumn = new TreeTableColumn<>("RunState");
        runStateCollumn.setCellValueFactory(MissionPane.nullSafe(StrandLine::runStateProperty));

        strandTableView.getColumns().addAll(idColumn, runStateCollumn);
        strandTableView.getColumns().forEach(c -> c.setSortable(false));

        strandTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        strandTableView.getSelectionModel().selectedItemProperty().addListener((e, o, n) -> updateButtonStates());
        strandTableView.setPrefWidth(220);

        Node buttonsPane = createButtonsPane();

        BorderPane box = new BorderPane();
        box.setCenter(strandTableView);
        box.setTop(buttonsPane);
        return box;
    }


    private void updateButtonStates() {
        clearListeners();
        Set<StrandCommand> strandCommands = allowedCommands();
        this.commandButtons.entrySet().forEach(e -> {
            FormattedButton button = e.getValue();
            if (strandCommands.contains(e.getKey())) {
                button.getButton().setDisable(false);
                listenFor(button.getKeyCode());
            } else {
                button.getButton().setDisable(true);
            }
        });

    }

	/*
	 * private MissionHandle getThisMissionHandle() { return
	 * this.missionHandle.get(); }
	 */
    private StrandCommand getCommandFromKeycode(KeyCode kc) {

        switch (kc.getName()) {
            case "F2":
                return StrandCommand.PAUSE;
            case "F6":
                return StrandCommand.STEP_INTO;
            case "F7":
                return StrandCommand.STEP_OVER;
            case "F8":
                return StrandCommand.SKIP;
            case "F5":
                return StrandCommand.RESUME;
            default:
                return StrandCommand.PAUSE;
        }

    }

    private void listenFor(KeyCode kc) {

        EventHandler<KeyEvent> filter = new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                Strand strand = selectedStrand();
                mole.instruct(missionHandle, strand, getCommandFromKeycode(kc));
            }
        };

        eventsList.add(filter);
        this.addEventFilter(KeyEvent.KEY_PRESSED, filter);

    }

    private void clearListeners() {
        for (EventHandler<KeyEvent> e : eventsList) {
            this.removeEventFilter(KeyEvent.KEY_PRESSED, e);
        }
    }

    private Set<StrandCommand> allowedCommands() {
        Strand strand = selectedStrand();
        MissionState state = lastState.getValue();
        if ((strand == null) || (state == null)) {
            return Collections.emptySet();
        }
        return state.allowedCommandsFor(strand);
    }

    private VBox createButtonsPane() {
        for (StrandCommand command : StrandCommand.values()) {
            FormattedButton button = commandButton(command);
            this.commandButtons.put(command, button);
        }
        VBox buttonsPane = new VBox();
        buttonsPane.setPadding(new Insets(10));
        Arrays.stream(StrandCommand.values()).map(commandButtons::get).forEach(b -> buttonsPane.getChildren().add(b.getButton()));
        updateButtonStates();
        return buttonsPane;
    }
    
}
