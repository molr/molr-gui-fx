/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.widgets;

import io.molr.commons.domain.*;
import io.molr.gui.fx.FxThreadScheduler;
import io.molr.gui.fx.widgets.breakpoints.BlockAttributeCell;
import io.molr.gui.fx.widgets.breakpoints.BreakpointCell;
import io.molr.gui.fx.widgets.breakpoints.EnabledBlockAttributeCellData;
import io.molr.gui.fx.widgets.progress.Progress;
import io.molr.gui.fx.widgets.progress.TextProgressBarTreeTableCell;
import io.molr.mole.core.api.Mole;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class MissionPane extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionPane.class);

    private final Mission mission;
    private final Map<String, ExecutableLine> lines = new HashMap<>();

    private TreeTableView<ExecutableLine> blockTableView;

    private BooleanProperty autoFollow = new SimpleBooleanProperty(true);

    private final AtomicReference<MissionHandle> missionHandle = new AtomicReference<>();

    private final SimpleObjectProperty<MissionState> lastState = new SimpleObjectProperty<>();

    private final Mole mole;

    private final MonoProcessor<MissionHandle> handleProcessor = MonoProcessor.create();
    
    Mono<MissionHandle> missionHandle() {
    	return handleProcessor;
    }

    MissionPane(Mole mole, Mission mission, MissionHandle missionHandle) {
        this.mole = requireNonNull(mole, "mole must not be null");
        this.mission = requireNonNull(mission, "mission must not be null");
        this.missionHandle.set(requireNonNull(missionHandle, "missionInstance must not be null"));
        init();
    }

    private TreeItem<ExecutableLine> createTree(MissionRepresentation representation) {
    	/*
    	 * clear existing lines since existing lines will not be overridden
    	 */
    	lines.clear();
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

    private void init() {

        blockTableView = new TreeTableView<>();
        blockTableView.setTableMenuButtonVisible(true);
        blockTableView.setShowRoot(true);

        BorderPane blocksBox = new BorderPane();
        blocksBox.setCenter(blockTableView);
        blocksBox.setBottom(createBlockTableOptions());

        setCenter(blocksBox);
        //setRight(createStrandCommandPane());


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

        configureForInstance(missionHandle.get());

        updateRepresentation(mole.representationOf(this.mission).block());
    }

    private void configureForInstance(MissionHandle handle) {
        Flux<MissionState> missionStateFlux = mole.statesFor(handle).publishOn(FxThreadScheduler.instance());
        missionStateFlux.subscribe(this::updateStates, error -> {
        }, this::onStatesComplete);

        mole.representationsFor(handle).publishOn(FxThreadScheduler.instance()).subscribe(this::updateRepresentation);

        addInstanceColumns();
    }
    
    private void onStatesComplete() {
        
        /*
         * how should we detect and handle, that mission has been disposed in mission pane
         */
        this.setDisable(false);
    }

    private void updateRepresentation(MissionRepresentation representation) {
        TreeItem<ExecutableLine> newRoot = createTree(representation);
        this.blockTableView.setRoot(newRoot);
        Optional.ofNullable(lastState.get()).ifPresent(this::cursorAndFollow);
    }

    private void updateStates(MissionState missionState) {
       
        this.lastState.set(missionState);

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
                    EnabledBlockAttributeCellData breakpointCellData = new EnabledBlockAttributeCellData(allowedBlockCommands, breakpoint);
                    ExecutableLine line = e.getValue();
                    line.breakpointProperty().set(breakpointCellData);
                    
                    boolean ignoreBlock = missionState.ignoreBlockIds().contains(blockId);
                    EnabledBlockAttributeCellData ignoreCellData = new EnabledBlockAttributeCellData(allowedBlockCommands, ignoreBlock);
                    e.getValue().ignoreProperty().set(ignoreCellData);
                });
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

        TreeTableColumn<ExecutableLine, EnabledBlockAttributeCellData> breakpointColumn = new TreeTableColumn<>("Breakpoint");
        breakpointColumn.setPrefWidth(60);
        breakpointColumn.setCellValueFactory(nullSafe(ExecutableLine::breakpointProperty));
        breakpointColumn.setCellFactory(new Callback<TreeTableColumn<ExecutableLine,EnabledBlockAttributeCellData>, TreeTableCell<ExecutableLine,EnabledBlockAttributeCellData>>() {   
            @Override
            public TreeTableCell<ExecutableLine, EnabledBlockAttributeCellData> call(TreeTableColumn<ExecutableLine, EnabledBlockAttributeCellData> param) {
                return new BreakpointCell(mole, missionHandle.get());
            }
        });
        
        TreeTableColumn<ExecutableLine, EnabledBlockAttributeCellData> ignoreColumn = new TreeTableColumn<>("Ignore");
        ignoreColumn.setPrefWidth(60);
        ignoreColumn.setCellValueFactory(nullSafe(ExecutableLine::ignoreProperty));
        ignoreColumn.setCellFactory(new Callback<TreeTableColumn<ExecutableLine,EnabledBlockAttributeCellData>, TreeTableCell<ExecutableLine,EnabledBlockAttributeCellData>>() {   
            @Override
            public TreeTableCell<ExecutableLine, EnabledBlockAttributeCellData> call(TreeTableColumn<ExecutableLine, EnabledBlockAttributeCellData> param) {
                return new BlockAttributeCell(mole, missionHandle.get(), BlockCommand.SET_IGNORE, BlockCommand.UNSET_IGNORE);
            }
        });
        
        blockTableView.getColumns().add(2, cursorColumn);
        blockTableView.getColumns().addAll(ignoreColumn, breakpointColumn, progressColumn, runStateColumn, statusColumn);
        blockTableView.getColumns().forEach(c -> c.setSortable(false));
    }


    public static final <S, T> Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> nullSafe(Function<S, ObservableValue<T>> mapper) {
        return param -> Optional.ofNullable(param.getValue())
                .map(e -> e.getValue())
                .map(mapper)
                .orElse(null);
    }

}
