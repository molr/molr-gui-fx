/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx.widgets;

import io.molr.commons.domain.Block;
import io.molr.commons.domain.Result;
import io.molr.commons.domain.RunState;
import io.molr.gui.fx.widgets.breakpoints.BreakpointCellData;
import io.molr.gui.fx.widgets.progress.Progress;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import static io.molr.commons.domain.RunState.FINISHED;
import static io.molr.commons.domain.RunState.RUNNING;

public class ExecutableLine {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.FRANCE)
            .withZone(ZoneId.systemDefault());

    private final Block block;
    private final SimpleObjectProperty<RunState> runState = new SimpleObjectProperty<>(this, "runState",
            RunState.NOT_STARTED);
    private final SimpleObjectProperty<Result> state = new SimpleObjectProperty<>(this, "state", Result.UNDEFINED);
    private final SimpleObjectProperty<Progress> progress = new SimpleObjectProperty<>(this, "progress", Progress.undefined());
    private final SimpleStringProperty cursor = new SimpleStringProperty(this, "cursor");
    private final SimpleObjectProperty<BreakpointCellData> breakpointCellData = new SimpleObjectProperty<>(this, "breakpoint", BreakpointCellData.undefined());
    private final SimpleObjectProperty<BreakpointCellData> ignoreCellData = new SimpleObjectProperty<>(this, "ignore", BreakpointCellData.undefined());

    private final ReadOnlyStringProperty name;
    private final ReadOnlyStringProperty id;

    public <T> ExecutableLine(Block block) {
        this.block = Objects.requireNonNull(block, "block must not be null");
        this.name = new ReadOnlyStringWrapper(this, "name", block.text());
        this.id = new ReadOnlyStringWrapper(this, "id", block.id());
        configureAsLeaf();
    }

    private void configureAsLeaf() {
        progress.bind(Bindings.createObjectBinding(() -> {
            Result result = resultProperty().getValue();

            RunState rs = runStateProperty().getValue();
            if (RunState.NOT_STARTED == rs) {
                return new Progress(0.0, "", result);
            } else if (FINISHED == rs) {
                return new Progress(1.0, "" + Objects.toString(rs).toLowerCase(), result);
            } else if (RUNNING == rs) {
                return new Progress(-1.0, "" + Objects.toString(rs).toLowerCase(), result);
            } else {
                return new Progress(0.05, "" + Objects.toString(rs).toLowerCase(), result);
            }
        }, resultProperty(), runStateProperty()));

    }

    public Block executable() {
        return block;
    }

    public SimpleObjectProperty<Result> resultProperty() {
        return state;
    }

    public ReadOnlyStringProperty nameProperty() {
        return this.name;
    }

    public ReadOnlyStringProperty idProperty() {
        return this.id;
    }

    public SimpleObjectProperty<Progress> progressProperty() {
        return this.progress;
    }

    public SimpleObjectProperty<RunState> runStateProperty() {
        return runState;
    }

    public SimpleStringProperty cursorProperty() {
        return cursor;
    }

    public SimpleObjectProperty<BreakpointCellData> breakpointProperty() {
        return this.breakpointCellData;
    }

    public SimpleObjectProperty<BreakpointCellData> ignoreProperty() {
        return this.ignoreCellData;
    }
    
}
