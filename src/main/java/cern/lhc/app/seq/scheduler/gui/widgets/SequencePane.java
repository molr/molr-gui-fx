/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static io.reactivex.rxjavafx.schedulers.JavaFxScheduler.platform;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.layout.BorderPane;

@Component
@Scope(value = "prototype")
/*
 * NOTE: Since this is a spring prototype, the event listener mechanism of spring cannot be consistently used here,
 * because each event would produce a new prototype! Therefore we use rx streams here which are injected through the
 * ExecutableAdapter
 */
public class SequencePane extends BorderPane {

    private final ExecutionBlock executableRoot;
    private final Map<ExecutionBlock, ExecutableLine> lines = new HashMap<>();
    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ExecutableAdapter executableAdapter;

    @Autowired
    private ExecutableStatisticsProvider executableStatisticsProvider;

    public SequencePane(ExecutionBlock executableRoot) {
        this.executableRoot = requireNonNull(executableRoot, "executableRoot must not be null");
    }

    private TreeItem<ExecutableLine> createTree() {
        return nodeFor(executableRoot);
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
        TreeItem<ExecutableLine> root = createTree();

        executableAdapter.runStateChanges().observeOn(platform()).subscribe(this::updateRunState);
        executableAdapter.resultChanges().observeOn(platform()).subscribe(this::updateResult);

        TreeTableColumn<ExecutableLine, String> executableColumn = new TreeTableColumn<>("ExecutionBlock");
        executableColumn.setPrefWidth(600);
        executableColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

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

        TreeTableView<ExecutableLine> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().setAll(executableColumn, runStateColumn, statusColumn, progressColumn,
                commentColumn);

        treeTableView.setShowRoot(false);
        setCenter(treeTableView);

        scheduled.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                Instant now = Instant.now();
                for (ExecutableLine line : lines.values()) {
                    line.actualTimeProperty().set(now);
                }
            });
        } , 0, 500, MILLISECONDS);

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
