/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.minifx.fxcommons.util.Fillers;
import org.molr.commons.domain.Result;
import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import cern.lhc.app.seq.scheduler.util.FormattedButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.molr.commons.domain.*;
import org.molr.commons.domain.RunState;
import org.molr.agency.core.Agency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
    private final MissionRepresentation missionRepresentation;
    private final MissionParameterDescription description;
    private final Map<Block, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    private TreeTableView<ExecutableLine> blockTableView;
    private TreeTableView<StrandLine> strandTableView;
    private TextArea output;

    private BooleanProperty autoFollow = new SimpleBooleanProperty(true);

    private VBox instanceInfo;
    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    private final SimpleObjectProperty<MissionState> lastState = new SimpleObjectProperty<>();

    private final Map<StrandCommand, Button> commandButtons = new EnumMap<StrandCommand, Button>(StrandCommand.class);

    @Autowired
    private Agency agency;

    @Autowired
    private ExecutableStatisticsProvider executableStatisticsProvider;

    public MissionPane(Mission mission, MissionRepresentation missionRepresentation, MissionParameterDescription description) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.missionRepresentation = requireNonNull(missionRepresentation, "missionRepresentation must not be null");
        this.description = requireNonNull(description, "description must not be null");
    }

    public MissionPane(Mission mission, MissionRepresentation missionRepresentation, MissionParameterDescription description, MissionHandle missionHandle) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.missionRepresentation = requireNonNull(missionRepresentation, "missionRepresentation must not be null");
        this.description = requireNonNull(description, "description must not be null");
        this.missionHandle.set(requireNonNull(missionHandle, "missionInstance must not be null"));
    }

    private TreeItem<ExecutableLine> createTree() {
        return nodeFor(missionRepresentation.rootBlock());
    }

    private TreeItem<ExecutableLine> nodeFor(Block block) {
        ExecutableLine line = new ExecutableLine(block);
        executableStatisticsProvider.expectedDurationFor(block).ifPresent(line.usualDurationProperty()::set);
        lines.put(block, line);
        TreeItem<ExecutableLine> item = new TreeItem<>(line);
        item.getChildren().addAll(nodesFor(childrenOf(block)));
        return item;
    }

    private List<? extends Block> childrenOf(Block block) {
        return missionRepresentation.childrenOf(block);
    }

    private List<TreeItem<ExecutableLine>> nodesFor(List<? extends Block> executables) {
        return executables.stream().map(this::nodeFor).collect(toList());
    }

    @PostConstruct
    public void init() {
        instanceInfo = new VBox(10);
        instanceInfo.setPadding(new Insets(10, 10, 10, 10));
        setTop(new TitledPane("Mission Instance", instanceInfo));

        TreeItem<ExecutableLine> root = createTree();
        blockTableView = new TreeTableView<>(root);
        blockTableView.setShowRoot(false);
        setCenter(blockTableView);

        TreeTableColumn<ExecutableLine, String> executableColumn = new TreeTableColumn<>("Block");
        executableColumn.setPrefWidth(300);
        executableColumn.setCellValueFactory(nullSafe(ExecutableLine::nameProperty));
        executableColumn.setSortable(false);

        blockTableView.getColumns().add(executableColumn);

        MissionHandle handle = missionHandle.get();
        if (handle != null) {
            configureForInstance(handle);
        } else {
            configureInstantiable();
        }

    }

    private void configureInstantiable() {
        ParameterEditor parameterEditor = new ParameterEditor(description.parameters());
        instanceInfo.getChildren().add(parameterEditor);

        Button instantiateButton = new FormattedButton().getButton("Instantiate", "Instantiate", "Blue");
        instantiateButton.setOnAction(event -> {
            instantiateButton.setDisable(true);
            this.instantiate(parameterEditor.parameterValues());
        });
        instanceInfo.getChildren().add(instantiateButton);
    }

    private void instantiate(Map<String, Object> params) {
        agency.instantiate(mission, params).publishOn(fxThread()).subscribe(h -> {
            this.missionHandle.set(h);
            configureForInstance(h);
        });
    }


    private void configureForInstance(MissionHandle handle) {
        instanceInfo.getChildren().setAll(new Label(handle.toString()));

        agency.statesFor(handle).publishOn(fxThread()).subscribe(this::updateStates);
        agency.outputsFor(handle).publishOn(fxThread()).subscribe(this::updateOutput);

        addInstanceColumns();
        scheduled.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                Instant now = Instant.now();
                for (ExecutableLine line : lines.values()) {
                    line.actualTimeProperty().set(now);
                }
            });
        }, 0, 500, MILLISECONDS);

        setBottom(createBottomPane());
    }

    private void updateStates(MissionState missionState) {
        this.lastState.set(missionState);
        Strand lastSelectedStrandNode = selectedStrand();

        TreeItem<StrandLine> rootItem = treeFor(missionState);
        strandTableView.setRoot(rootItem);

        TreeItem<StrandLine> toBeSelected = find(rootItem, lastSelectedStrandNode);

        if (toBeSelected != null) {
            strandTableView.getSelectionModel().select(toBeSelected);
        } else {
            strandTableView.getSelectionModel().select(rootItem);
        }

        for (ExecutableLine line : lines.values()) {
            line.cursorProperty().set("");
        }
        for (Strand strand : missionState.allStrands()) {
            Optional<Block> cursor = missionState.cursorPositionIn(strand);
            if (cursor.isPresent()) {
                lines.get(cursor.get()).cursorProperty().set(strand.id() + "->");
            }
        }

        if (autoFollow.get()) {
            collapse(blockTableView.getRoot());
            for (Strand strand : missionState.allStrands()) {
                Optional<Block> cursor = missionState.cursorPositionIn(strand);
                if (cursor.isPresent()) {
                    expandParents(blockTableView.getRoot(), cursor.get());
                }
            }
        }

        lines.entrySet().forEach(e -> {
            Result result = missionState.resultOf(e.getKey());
            e.getValue().resultProperty().set(result);
        });

        lines.entrySet().forEach(e -> {
            RunState result = missionState.runStateOf(e.getKey());
            e.getValue().runStateProperty().set(result);
        });


        updateButtonStates();
    }


    private void collapse(TreeItem<ExecutableLine> subTree) {
        for (TreeItem<ExecutableLine> child : subTree.getChildren()) {
            collapse(child);
        }
        subTree.setExpanded(false);
    }


    private boolean expandParents(TreeItem<ExecutableLine> subTree, Block block) {
        if (block.equals(subTree.getValue().executable())) {
            expandParents(subTree.getParent());
            return true;
        } else {
            for (TreeItem<ExecutableLine> child : subTree.getChildren()) {
                if (expandParents(child, block)) {
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

    private TreeItem<StrandLine> treeFor(MissionState state) {
        Optional<Strand> rootStrand = state.rootStrand();
        if (rootStrand.isPresent()) {
            return treeItemFor(rootStrand.get(), state);
        }
        return new TreeItem<>();
    }

    private TreeItem<StrandLine> treeItemFor(Strand parent, MissionState state) {
        TreeItem<StrandLine> item = new TreeItem<>(new StrandLine(parent, state.runStateOf(parent)));
        Set<TreeItem<StrandLine>> childNodes = state.childrenOf(parent).stream().map(s -> treeItemFor(s, state)).collect(toSet());
        item.getChildren().addAll(childNodes);
        return item;
    }

    private Pane createBottomPane() {
        BorderPane bottomPane = new BorderPane();
        strandTableView = new TreeTableView<>();
        bottomPane.setLeft(strandTableView);

        TreeTableColumn<StrandLine, String> idColumn = new TreeTableColumn<>("Strand");
        idColumn.setCellValueFactory(nullSafe(StrandLine::idProperty));

        TreeTableColumn<StrandLine, RunState> runStateCollumn = new TreeTableColumn<>("RunState");
        runStateCollumn.setCellValueFactory(nullSafe(StrandLine::runStateProperty));

        strandTableView.getColumns().addAll(idColumn, runStateCollumn);
        strandTableView.getColumns().forEach(c -> c.setSortable(false));

        strandTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        strandTableView.getSelectionModel().selectedItemProperty().addListener((e, o, n) -> updateButtonStates());

        HBox buttonsPane = createButtonsPane();
        bottomPane.setTop(buttonsPane);

        this.output = new TextArea();
        bottomPane.setCenter(output);

        return bottomPane;
    }


    private void updateButtonStates() {
        Set<StrandCommand> strandCommands = allowedCommands();
        this.commandButtons.entrySet().forEach(e -> {
            Button button = e.getValue();
            if (strandCommands.contains(e.getKey())) {
                button.setDisable(false);
            } else {
                button.setDisable(true);
            }
        });

    }

    private Set<StrandCommand> allowedCommands() {
        Strand strand = selectedStrand();
        MissionState state = lastState.getValue();
        if ((strand == null) || (state == null)) {
            return Collections.emptySet();
        }
        return state.allowedCommandsFor(strand);
    }


    private HBox createButtonsPane() {
        for (StrandCommand command : StrandCommand.values()) {
            Button button = commandButton(command);
            this.commandButtons.put(command, button);
        }
        HBox buttonsPane = new HBox();
        Arrays.stream(StrandCommand.values()).map(commandButtons::get).forEach(buttonsPane.getChildren()::add);
        buttonsPane.getChildren().add(Fillers.horizontalFiller());
        CheckBox autoFollowCheckbox = new CheckBox("Automatically Expand/Collapse");
        autoFollowCheckbox.selectedProperty().bindBidirectional(this.autoFollow);
        buttonsPane.getChildren().add(autoFollowCheckbox);
        updateButtonStates();
        return buttonsPane;
    }

    private Button commandButton(StrandCommand command) {
        Button button = new FormattedButton().getAndGuessButton(command.toString());
        button.setMnemonicParsing(false);
        button.setOnAction(event -> {
            Strand strand = selectedStrand();
            agency.instruct(this.missionHandle.get(), strand, command);
        });
        return button;
    }

    private void addInstanceColumns() {
        TreeTableColumn<ExecutableLine, String> cursorColumn = new TreeTableColumn<>("Cursor");
        cursorColumn.setPrefWidth(60);
        /* For some reason there appeared a nullpointer exception here .. unclear*/
        cursorColumn.setCellValueFactory(nullSafe(ExecutableLine::cursorProperty));

        TreeTableColumn<ExecutableLine, RunState> runStateColumn = new TreeTableColumn<>("RunState");
        runStateColumn.setPrefWidth(100);
        runStateColumn.setCellValueFactory(nullSafe(ExecutableLine::runStateProperty));

        TreeTableColumn<ExecutableLine, Result> statusColumn = new TreeTableColumn<>("Result");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellValueFactory(nullSafe(ExecutableLine::resultProperty));

        TreeTableColumn<ExecutableLine, Double> progressColumn = new TreeTableColumn<>("Progress");
        progressColumn.setPrefWidth(200);
        progressColumn.setCellValueFactory(nullSafe(ExecutableLine::progressProperty));
        progressColumn.setCellFactory(ProgressBarTreeTableCell.forTreeTableColumn());

        TreeTableColumn<ExecutableLine, String> commentColumn = new TreeTableColumn<>("Comment");
        commentColumn.setPrefWidth(300);
        commentColumn.setCellValueFactory(nullSafe(ExecutableLine::commentProperty));

        blockTableView.getColumns().add(0, cursorColumn);
        blockTableView.getColumns().addAll(runStateColumn, statusColumn, progressColumn, commentColumn);
        blockTableView.getColumns().forEach(c -> c.setSortable(false));
    }


    public static final <S, T> Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> nullSafe(Function<S, ObservableValue<T>> mapper) {
        return param -> Optional.ofNullable(param.getValue())
                .map(e -> e.getValue())
                .map(mapper)
                .orElse(null);
    }

}
