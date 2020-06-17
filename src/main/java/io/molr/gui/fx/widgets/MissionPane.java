/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.widgets;

import io.molr.commons.domain.*;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.util.FormattedButton;
import io.molr.gui.fx.widgets.breakpoints.BreakpointCell;
import io.molr.gui.fx.widgets.breakpoints.BreakpointCellData;
import io.molr.gui.fx.widgets.progress.Progress;
import io.molr.gui.fx.widgets.progress.TextProgressBarTreeTableCell;
import io.molr.mole.core.api.Mole;
import io.molr.mole.remote.rest.ConnectException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import reactor.core.publisher.Flux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
@Scope(value = "prototype")
/*
 * NOTE: Since this is a spring prototype, the event listener mechanism of spring cannot be consistently used here,
 * because each event would produce a new prototype! Therefore we use rx streams here which are injected through the
 * ExecutableAdapter
 */
public class MissionPane extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionPane.class);

    private final Mission mission;
    private final MissionParameterDescription description;
    private final Map<String, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
    private List<EventHandler> eventsList = new ArrayList<EventHandler>();

    private TreeTableView<ExecutableLine> blockTableView;
    private TreeTableView<StrandLine> strandTableView;
    private TextArea output;

    private BooleanProperty autoFollow = new SimpleBooleanProperty(true);

    private VBox instanceInfo;
    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    private final SimpleObjectProperty<MissionState> lastState = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<MissionRepresentation> lastRepresentation = new SimpleObjectProperty<>();

    private final Map<StrandCommand, FormattedButton> commandButtons = new EnumMap<StrandCommand, FormattedButton>(StrandCommand.class);
    
    private FormattedButton disposeButton;

    @Autowired
    private Mole mole;

    public MissionPane(Mission mission, MissionParameterDescription description) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.description = requireNonNull(description, "description must not be null");

    }

    public MissionPane(Mission mission, MissionParameterDescription description, MissionHandle missionHandle) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.description = requireNonNull(description, "description must not be null");
        this.missionHandle.set(requireNonNull(missionHandle, "missionInstance must not be null"));
    }

    private TreeItem<ExecutableLine> createTree(MissionRepresentation representation) {
        return nodeFor(representation, representation.rootBlock());
    }

    private TreeItem<ExecutableLine> nodeFor(MissionRepresentation representation, Block block) {
        ExecutableLine line = lines.computeIfAbsent(block.id(), b -> new ExecutableLine(block));
        TreeItem<ExecutableLine> item = new TreeItem<>(line);
        item.getChildren().addAll(nodesFor(representation, representation.childrenOf(block)));
        return item;
    }

    private List<TreeItem<ExecutableLine>> nodesFor(MissionRepresentation representation, List<? extends Block> executables) {
        return executables.stream().map(b -> this.nodeFor(representation, b)).collect(toList());
    }

    @PostConstruct
    public void init() {
        instanceInfo = new VBox(10);
        instanceInfo.setPadding(new Insets(10, 10, 10, 10));
        setTop(new TitledPane("Mission Instance", instanceInfo));

        blockTableView = new TreeTableView<>();
        blockTableView.setTableMenuButtonVisible(true);
        blockTableView.setShowRoot(true);

        BorderPane blocksBox = new BorderPane();
        blocksBox.setCenter(blockTableView);
        blocksBox.setBottom(createBlockTableOptions());

        setCenter(blocksBox);
        setRight(createStrandCommandPane());


        TreeTableColumn<ExecutableLine, String> idColumn = new TreeTableColumn<>("id");
        idColumn.setPrefWidth(70);
        idColumn.setCellValueFactory(nullSafe(ExecutableLine::idProperty));
        idColumn.setVisible(false);
        idColumn.setSortable(false);

        TreeTableColumn<ExecutableLine, String> executableColumn = new TreeTableColumn<>("Block");
        executableColumn.setPrefWidth(300);
        executableColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        executableColumn.setSortable(false);

        blockTableView.getColumns().addAll(idColumn, executableColumn);

        MissionHandle handle = missionHandle.get();
        if (handle != null) {
            configureForInstance(handle);
        } else {
            configureInstantiable();
        }

        updateRepresentation(mole.representationOf(this.mission).block());
    }

    private void configureInstantiable() {
        ParameterEditor parameterEditor = new ParameterEditor(description.parameters());
        instanceInfo.getChildren().add(parameterEditor);

        FormattedButton instantiateButton = new FormattedButton("Instantiate", "Instantiate", "Blue");
        instantiateButton.getButton().setOnAction(event -> {
            instantiateButton.getButton().setDisable(true);
            this.instantiate(parameterEditor.parameterValues());
        });
        instanceInfo.getChildren().add(instantiateButton.getButton());
    }

    private void instantiate(Map<String, Object> params) {
        mole.instantiate(mission, params).publishOn(FxThreadScheduler.instance()).subscribe(h -> {
            this.missionHandle.set(h);
            configureForInstance(h);
        });
    }


    private void configureForInstance(MissionHandle handle) {
        instanceInfo.getChildren().setAll(new Label(handle.toString()));
        disposeButton = new FormattedButton("Dispose", "Instantiate", "Blue");
        disposeButton.getButton().setDisable(true);
        disposeButton.getButton().setOnAction(event -> {
            mole.instruct(handle, MissionCommand.DISPOSE);
        });
        instanceInfo.getChildren().add(disposeButton.getButton());

        Flux<MissionOutput> missionOutputsFlux = mole.outputsFor(handle).publishOn(FxThreadScheduler.instance());
        missionOutputsFlux.subscribe(this::updateOutput, error->{} , this::onOutputsComplete);
        
        Flux<MissionState> missionStateFlux = mole.statesFor(handle).publishOn(FxThreadScheduler.instance());
        missionStateFlux.doOnError(ConnectException.class, this::onInstanceConnectionError).subscribe(this::updateStates, error->{}, this::onStatesComplete);

        mole.representationsFor(handle).publishOn(FxThreadScheduler.instance()).subscribe(this::updateRepresentation);

        addInstanceColumns();

        setBottom(createOutput());
    }
    

    private void onInstanceConnectionError(Throwable error) {
    	this.setDisabled(true);
    	this.setCenter(new Text("Mission instance is OFFLINE, the connection has been lost. Please close this tab and try to reconnect manually."));
    }
    
    private void onOutputsComplete() {
        /**
         * TODO use case?
         */
    }
    
    private void onStatesComplete() {
        /*
         * how should we detect and handle, that mission has been disposed in mission pane
         */
        this.setDisable(true);
        this.setCenter(new Text("Mission instance is COMPLETE."));
    }

    private TitledPane createOutput() {
        this.output = new TextArea();
        return new TitledPane("Output", this.output);
    }

    private void updateRepresentation(MissionRepresentation representation) {
        TreeItem<ExecutableLine> newRoot = createTree(representation);
        this.blockTableView.setRoot(newRoot);
        Optional.ofNullable(lastState.get()).ifPresent(this::cursorAndFollow);
    }

    private void updateStates(MissionState missionState) {
        boolean disableDisposeButton = !missionState.allowedMissionCommands().contains(MissionCommand.DISPOSE);
        disposeButton.getButton().setDisable(disableDisposeButton);
        
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

        cursorAndFollow(missionState);

        lines.entrySet().
                forEach(e -> {
                    Result result = missionState.resultOfBlockId(e.getKey());
                    e.getValue().resultProperty().set(result);
                });

        lines.entrySet().
                forEach(e -> {
                    RunState result = missionState.runStateOfBlockId(e.getKey());
                    e.getValue().runStateProperty().set(result);

                    String blockId = e.getKey();
                    Set<BlockCommand> allowedBlockCommands = missionState.allowedBlockCommandsFor(blockId);
                    boolean breakpoint = missionState.breakpointBlockIds().contains(blockId);
                    BreakpointCellData breakpointCellData = new BreakpointCellData(allowedBlockCommands, breakpoint);
                    ExecutableLine line = e.getValue();
                    line.breakpointProperty().set(breakpointCellData);  
                });


        updateButtonStates();
    }

    private void cursorAndFollow(MissionState missionState) {
        for (ExecutableLine line : lines.values()) {
            line.cursorProperty().set("");
        }
        for (Strand strand : missionState.allStrands()) {
            Optional<String> cursorBlockId = missionState.cursorBlockIdIn(strand);
            if (cursorBlockId.isPresent()) {
                ExecutableLine line = lines.get(cursorBlockId.get());
                if (line == null) {
                    LOGGER.warn("No line for block {} available. Cannot set cursor.", cursorBlockId.get());
                } else {
                    line.cursorProperty().set("<-" + strand.id() + "->");
                }
            }
        }

        if (autoFollow.get()) {
            /*
             * TODO the collapseChildrenOf collapses block lines on each MissionState update.
             * The following expansion depending on cursorBlockIds is not suitable every use case, e.g. breakpoint
             * updates (e.g. lines are collapsed after SET_BREAKPOINT/UNSET_BREAKPOINT).
             */
            collapseChildreanOf(blockTableView.getRoot());
            for (Strand strand : missionState.allStrands()) {
                Optional<String> cursorBlockId = missionState.cursorBlockIdIn(strand);
                if (cursorBlockId.isPresent()) {
                    boolean found = expandParents(blockTableView.getRoot(), cursorBlockId.get());
                }
            }
        }
    }

    private void collapse(TreeItem<ExecutableLine> subTree) {
        collapseChildreanOf(subTree);
        subTree.setExpanded(false);
    }

    private void collapseChildreanOf(TreeItem<ExecutableLine> subTree) {
        for (TreeItem<ExecutableLine> child : subTree.getChildren()) {
            collapse(child);
        }
    }

    private boolean expandParents(TreeItem<ExecutableLine> subTree, String blockId) {
        if (blockId.equals(subTree.getValue().executable().id())) {
            expandParents(subTree.getParent());
            return true;
        } else {
            for (TreeItem<ExecutableLine> child : subTree.getChildren()) {
                if (expandParents(child, blockId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void expandParents(TreeItem<ExecutableLine> selectedItem) {
        if (selectedItem != null) {
            expandParents(selectedItem.getParent());

            if (!selectedItem.isLeaf()) {
                selectedItem.setExpanded(true);
            }
        }
    }

    private void updateOutput(MissionOutput output) {
        this.output.setText(output.pretty());
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
        idColumn.setCellValueFactory(nullSafe(StrandLine::idProperty));

        TreeTableColumn<StrandLine, RunState> runStateCollumn = new TreeTableColumn<>("RunState");
        runStateCollumn.setCellValueFactory(nullSafe(StrandLine::runStateProperty));

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

    private MissionHandle getThisMissionHandle() {
        return this.missionHandle.get();
    }

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

        EventHandler filter = new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                Strand strand = selectedStrand();
                mole.instruct(getThisMissionHandle(), strand, getCommandFromKeycode(kc));
            }
        };

        eventsList.add(filter);
        this.addEventFilter(KeyEvent.KEY_PRESSED, filter);

    }

    private void clearListeners() {
        for (EventHandler e : eventsList) {
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

    private HBox createBlockTableOptions() {
        HBox box = new HBox(10);
        CheckBox autoFollowCheckbox = new CheckBox("Automatically Expand/Collapse");
        autoFollowCheckbox.selectedProperty().bindBidirectional(this.autoFollow);
        box.getChildren().add(autoFollowCheckbox);

        CheckBox showRootCheckbox = new CheckBox("Show root");
        showRootCheckbox.selectedProperty().bindBidirectional(blockTableView.showRootProperty());
        box.getChildren().add(showRootCheckbox);
        return box;
    }
    
    private FormattedButton commandButton(StrandCommand command) {
        FormattedButton button = new FormattedButton(command.toString());
        button.getButton().setPrefWidth(200);
        button.getButton().setMnemonicParsing(false);
        button.getButton().setOnAction(event -> {
            Strand strand = selectedStrand();
            mole.instruct(this.missionHandle.get(), strand, command);
        });
        return button;
    }

    private void addInstanceColumns() {
        TreeTableColumn<ExecutableLine, String> cursorColumn = new TreeTableColumn<>("Cursor");
        cursorColumn.setPrefWidth(60);
        cursorColumn.setCellValueFactory(nullSafe(ExecutableLine::cursorProperty));

        TreeTableColumn<ExecutableLine, RunState> runStateColumn = new TreeTableColumn<>("RunState");
        runStateColumn.setPrefWidth(100);
        runStateColumn.setCellValueFactory(nullSafe(ExecutableLine::runStateProperty));
        runStateColumn.setVisible(false);

        TreeTableColumn<ExecutableLine, Result> statusColumn = new TreeTableColumn<>("Result");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellValueFactory(nullSafe(ExecutableLine::resultProperty));
        statusColumn.setVisible(false);

        TreeTableColumn<ExecutableLine, Progress> progressColumn = new TreeTableColumn<>("Progress");
        progressColumn.setPrefWidth(200);
        progressColumn.setCellValueFactory(nullSafe(ExecutableLine::progressProperty));
        progressColumn.setCellFactory(TextProgressBarTreeTableCell.forTreeTableColumn());

        TreeTableColumn<ExecutableLine, BreakpointCellData> breakpointColumn = new TreeTableColumn<>("Breakpoint");
        breakpointColumn.setPrefWidth(60);
        breakpointColumn.setCellValueFactory(nullSafe(ExecutableLine::breakpointProperty));
        breakpointColumn.setCellFactory(new Callback<TreeTableColumn<ExecutableLine,BreakpointCellData>, TreeTableCell<ExecutableLine,BreakpointCellData>>() {   
            @Override
            public TreeTableCell<ExecutableLine, BreakpointCellData> call(TreeTableColumn<ExecutableLine, BreakpointCellData> param) {
                return new BreakpointCell(mole, missionHandle.get());
            }
        });
        
        blockTableView.getColumns().add(2, cursorColumn);
        blockTableView.getColumns().addAll(breakpointColumn, progressColumn, runStateColumn, statusColumn);
        blockTableView.getColumns().forEach(c -> c.setSortable(false));
    }


    public static final <S, T> Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> nullSafe(Function<S, ObservableValue<T>> mapper) {
        return param -> Optional.ofNullable(param.getValue())
                .map(e -> e.getValue())
                .map(mapper)
                .orElse(null);
    }

}
