/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
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
    private final Map<ExecutionBlock, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    private TreeTableView<ExecutableLine> treeTableView;
    private VBox instanceInfo;
    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    @Autowired
    private ExecutableAdapter executableAdapter;

    @Autowired
    private MolrService molrService;

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

    private TreeItem<ExecutableLine> nodeFor(ExecutionBlock l) {
        ExecutableLine line = new ExecutableLine(l);
        executableStatisticsProvider.expectedDurationFor(l).ifPresent(line.usualDurationProperty()::set);
        lines.put(l, line);
        TreeItem<ExecutableLine> item = new TreeItem<>(line);
        item.getChildren().addAll(nodesFor(l.children()));
        return item;
    }

    private List<TreeItem<ExecutableLine>> nodesFor(List<? extends ExecutionBlock> executables) {
        return executables.stream().map(this::nodeFor).collect(toList());
    }

    @PostConstruct
    public void init() {
        instanceInfo = new VBox(10);
        instanceInfo.setPadding(new Insets(10, 10, 10, 10));
        setTop(new TitledPane("Mission Instance", instanceInfo));

        TreeItem<ExecutableLine> root = createTree();
        treeTableView = new TreeTableView<>(root);
        treeTableView.setShowRoot(false);
        setCenter(treeTableView);

        TreeTableColumn<ExecutableLine, String> executableColumn = new TreeTableColumn<>("ExecutionBlock");
        executableColumn.setPrefWidth(600);
        executableColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        executableColumn.setSortable(false);

        treeTableView.getColumns().add(executableColumn);

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
        molrService.instantiate(this.missionDescription.mission(), Collections.emptyMap()).publishOn(FxSchedulers.fxThread()).subscribe(h -> {
            this.missionHandle.set(h);
            configureForInstance(h);
        });
    }


    private void configureForInstance(MissionHandle handle) {
        instanceInfo.getChildren().setAll(new Label(handle.toString()));

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

        setBottom(createButtonsPane());
    }

    private Pane createButtonsPane() {
        FlowPane buttonsPane = new FlowPane();
        buttonsPane.getChildren().addAll(new Button("step"), new Button("pause"), new Button("resume"));
        return buttonsPane;
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

        treeTableView.getColumns().addAll(runStateColumn, statusColumn, progressColumn, commentColumn);
        treeTableView.getColumns().forEach(c -> c.setSortable(false));
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
