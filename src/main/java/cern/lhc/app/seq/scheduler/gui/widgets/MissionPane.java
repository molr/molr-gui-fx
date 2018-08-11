/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static com.google.common.collect.ImmutableSetMultimap.toImmutableSetMultimap;
import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.molr.server.api.Agency;
import com.google.common.collect.ImmutableSetMultimap;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.molr.commons.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import javafx.application.Platform;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.layout.BorderPane;

@Component
@Scope(value = "prototype")
/*
 * NOTE: Since this is a spring prototype, the event listener mechanism of spring cannot be consistently used here,
 * because each event would produce a new prototype! Therefore we use rx streams here which are injected through the
 * ExecutableAdapter
 */
public class MissionPane extends BorderPane {

    private final MissionDescription missionDescription;
    private final Map<Block, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    private TreeTableView<ExecutableLine> blockTableView;
    private TreeTableView<Strand> strandTableView;

    private VBox instanceInfo;
    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    @Autowired
    private ExecutableAdapter executableAdapter;

    @Autowired
    private Agency agency;

    @Autowired
    private ExecutableStatisticsProvider executableStatisticsProvider;

    public MissionPane(MissionDescription missionDescription) {
        this.missionDescription = requireNonNull(missionDescription, "missionDescription must not be null");
    }

    public MissionPane(MissionDescription missionDescription, MissionHandle missionHandle) {
        this.missionDescription = requireNonNull(missionDescription, "missionDescription must not be null");
        this.missionHandle.set(requireNonNull(missionHandle, "missionHandle must not be null"));
    }

    private TreeItem<ExecutableLine> createTree() {
        return nodeFor(missionDescription.rootBlock());
    }

    private TreeItem<ExecutableLine> nodeFor(Block l) {
        ExecutableLine line = new ExecutableLine(l);
        executableStatisticsProvider.expectedDurationFor(l).ifPresent(line.usualDurationProperty()::set);
        lines.put(l, line);
        TreeItem<ExecutableLine> item = new TreeItem<>(line);
        item.getChildren().addAll(nodesFor(l.children()));
        return item;
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
        agency.instantiate(this.missionDescription.mission(), Collections.emptyMap()).publishOn(fxThread()).subscribe(h -> {
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

        /* TODO: update the states*/
    }

    private TreeItem<Strand> treeFor(Set<Strand> strands) {
        ImmutableSetMultimap<Strand, Strand> children = strands.stream().filter(s -> s.parent().isPresent()).collect(toImmutableSetMultimap(s -> s.parent().get(), identity()));

        List<Strand> roots = strands.stream().filter(s -> !s.parent().isPresent()).collect(toList());
        if (roots.size() != 1) {
            throw new IllegalArgumentException("More than one root strand (= strand without a parent) found in set " + strands + ".");
        }
        Strand root = roots.get(0);

        return treeItemFor(root, children);
    }

    private TreeItem<Strand> treeItemFor(Strand parent, ImmutableSetMultimap<Strand, Strand> children) {
        TreeItem<Strand> item = new TreeItem<>();
        Set<TreeItem<Strand>> childNodes = children.get(parent).stream().map(s -> treeItemFor(s, children)).collect(toSet());
        item.getChildren().addAll(childNodes);
        return item;
    }


    private Pane createBottomPane() {
        BorderPane bottomPane = new BorderPane();
        VBox buttonsPane = new VBox();
        buttonsPane.getChildren().addAll(new Button("step"), new Button("pause"), new Button("resume"));
        bottomPane.setLeft(buttonsPane);

        strandTableView = new TreeTableView<>();
        bottomPane.setCenter(strandTableView);

        TreeTableColumn<Strand, String> idColumn = new TreeTableColumn<>("Strand id");
        idColumn.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getValue().id()));

        TreeTableColumn<Strand, String> nameColumn = new TreeTableColumn<>("Strand name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getValue().name()));

        strandTableView.getColumns().addAll(idColumn, nameColumn);
        strandTableView.getColumns().forEach(c -> c.setSortable(false));
        return bottomPane;
    }

    private void addInstanceColumns() {
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

        blockTableView.getColumns().addAll(runStateColumn, statusColumn, progressColumn, commentColumn);
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
