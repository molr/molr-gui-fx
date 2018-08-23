/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import com.google.common.collect.ImmutableSetMultimap;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.molr.commons.api.domain.*;
import org.molr.commons.api.service.Agency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.ImmutableSetMultimap.toImmutableSetMultimap;
import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
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

    private final Mission mission;
    private final MissionRepresentation missionRepresentation;
    private final Map<Block, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    private TreeTableView<ExecutableLine> blockTableView;
    private TreeTableView<Strand> strandTableView;

    private VBox instanceInfo;
    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    private final SimpleObjectProperty<MissionState> lastState = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Strand> operationalStrand = new SimpleObjectProperty<>();

    @Autowired
    private ExecutableAdapter executableAdapter;

    @Autowired
    private Agency agency;

    @Autowired
    private ExecutableStatisticsProvider executableStatisticsProvider;

    public MissionPane(Mission mission, MissionRepresentation missionRepresentation) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.missionRepresentation = requireNonNull(missionRepresentation, "missionRepresentation must not be null");
    }

    public MissionPane(Mission mission, MissionRepresentation missionRepresentation, MissionHandle missionHandle) {
        this.mission = requireNonNull(mission, "mission must not be null");
        this.missionRepresentation = requireNonNull(missionRepresentation, "missionRepresentation must not be null");
        this.missionHandle.set(requireNonNull(missionHandle, "missionInstance must not be null"));
    }

    private TreeItem<ExecutableLine> createTree() {
        return nodeFor(missionRepresentation.rootBlock());
    }

    private TreeItem<ExecutableLine> nodeFor(Block l) {
        ExecutableLine line = new ExecutableLine(l);
        executableStatisticsProvider.expectedDurationFor(l).ifPresent(line.usualDurationProperty()::set);
        lines.put(l, line);
        TreeItem<ExecutableLine> item = new TreeItem<>(line);
        item.getChildren().addAll(nodesFor(childrenOf(l)));
        return item;
    }

    private List<? extends Block> childrenOf(Block l) {
        return missionRepresentation.childrenOf(l);
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

        TreeTableColumn<ExecutableLine, String> executableColumn = new TreeTableColumn<>("ExecutionBlock");
        executableColumn.setPrefWidth(600);
        executableColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
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
        Button instantiateButton = new Button("instantiate");
        instantiateButton.setOnAction(event -> {
            instantiateButton.setDisable(true);
            this.instantiate();
        });
        instanceInfo.getChildren().add(instantiateButton);
    }

    private void instantiate() {
        agency.instantiate(mission, Collections.emptyMap()).publishOn(fxThread()).subscribe(h -> {
            this.missionHandle.set(h);
            configureForInstance(h);
        });
    }


    private void configureForInstance(MissionHandle handle) {
        instanceInfo.getChildren().setAll(new Label(handle.toString()));

        agency.statesFor(handle).publishOn(fxThread()).subscribe(this::updateStates);

        executableAdapter.runStateChanges().subscribeOn(fxThread()).subscribe(this::updateRunState);
        executableAdapter.resultChanges().subscribeOn(fxThread()).subscribe(this::updateResult);

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
        TreeItem<Strand> strandTreeItem = treeFor(missionState.activeStrands());
        strandTableView.setRoot(strandTreeItem);

        for (ExecutableLine line : lines.values()) {
            line.cursorProperty().set("");
        }
        for (Strand strand : missionState.activeStrands()) {
            Optional<Block> cursor = missionState.cursorPositionIn(strand);
            if (cursor.isPresent()) {
                lines.get(cursor.get()).cursorProperty().set(strand.id() + "->");
            }
        }
        /* TODO: update the states*/
    }

    private TreeItem<Strand> treeFor(Set<Strand> strands) {
        ImmutableSetMultimap<String, Strand> children = strands.stream().filter(s -> s.parentId().isPresent()).collect(toImmutableSetMultimap(s -> s.parentId().get(), identity()));

        List<Strand> roots = strands.stream().filter(s -> !s.parentId().isPresent()).collect(toList());
        if (roots.isEmpty()) {
            throw new IllegalArgumentException("No root strand (= strand without a parent) found in set " + strands + ".");
        }
        if (roots.size() > 1) {
            throw new IllegalArgumentException("More than one root strand (= strand without a parent) found in set " + strands + ".");
        }
        Strand root = roots.get(0);

        return treeItemFor(root, children);
    }

    private TreeItem<Strand> treeItemFor(Strand parent, ImmutableSetMultimap<String, Strand> children) {
        TreeItem<Strand> item = new TreeItem<>(parent);
        Set<TreeItem<Strand>> childNodes = children.get(parent.id()).stream().filter(c -> !Objects.isNull(c)).map(s -> treeItemFor(s, children)).collect(toSet());
        item.getChildren().addAll(childNodes);
        return item;
    }

    private Pane createBottomPane() {
        BorderPane bottomPane = new BorderPane();
        VBox buttonsPane = createButtonsPane();
        bottomPane.setLeft(buttonsPane);

        strandTableView = new TreeTableView<>();
        bottomPane.setCenter(strandTableView);

        TreeTableColumn<Strand, String> idColumn = new TreeTableColumn<>("Strand");
        idColumn.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getValue().id()));

        strandTableView.getColumns().addAll(idColumn);
        strandTableView.getColumns().forEach(c -> c.setSortable(false));
        return bottomPane;
    }

    private VBox createButtonsPane() {
        VBox buttonsPane = new VBox();
        buttonsPane.getChildren().addAll(new Button("step"), new Button("pause"), new Button("resume"));
        return buttonsPane;
    }

    private void addInstanceColumns() {
        TreeTableColumn<ExecutableLine, String> cursorColumn = new TreeTableColumn<>("Cursor");
        cursorColumn.setPrefWidth(40);
        cursorColumn.setCellValueFactory(param -> param.getValue().getValue().cursorProperty());

        TreeTableColumn<ExecutableLine, RunState> runStateColumn = new TreeTableColumn<>("RunState");
        runStateColumn.setPrefWidth(100);
        runStateColumn.setCellValueFactory(param -> param.getValue().getValue().runStateProperty());

        TreeTableColumn<ExecutableLine, Result> statusColumn = new TreeTableColumn<>("Status");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellValueFactory(param -> param.getValue().getValue().stateProperty());

        TreeTableColumn<ExecutableLine, Double> progressColumn = new TreeTableColumn<>("Progress");
        progressColumn.setPrefWidth(200);
        progressColumn.setCellValueFactory(param -> param.getValue().getValue().progressProperty());
        progressColumn.setCellFactory(ProgressBarTreeTableCell.forTreeTableColumn());

        TreeTableColumn<ExecutableLine, String> commentColumn = new TreeTableColumn<>("Comment");
        commentColumn.setPrefWidth(300);
        commentColumn.setCellValueFactory(param -> param.getValue().getValue().commentProperty());

        blockTableView.getColumns().add(0, cursorColumn);
        blockTableView.getColumns().addAll( runStateColumn, statusColumn, progressColumn, commentColumn);
        blockTableView.getColumns().forEach(c -> c.setSortable(false));
    }

    public void updateRunState(RunStateChange change) {
        Optional.ofNullable(lines.get(change.executable())).ifPresent(l -> {
            Platform.runLater(() -> l.runStateProperty().set(change.runState()));
        });
    }

    public void updateResult(ResultChange change) {
        System.out.println("Received MissionEvent: " + change.executable());
        Optional.ofNullable(lines.get(change.executable())).ifPresent(l -> {
            Platform.runLater(() -> l.stateProperty().set(change.result()));
        });
    }


}
