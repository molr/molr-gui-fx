/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static org.molr.commons.domain.RunState.FINISHED;
import static cern.lhc.app.seq.scheduler.util.DurationFormats.shortLetters;
import static org.molr.commons.domain.RunState.RUNNING;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import cern.lhc.app.seq.scheduler.gui.widgets.progress.Progress;
import org.molr.commons.domain.Result;
import org.molr.commons.domain.Block;
import org.molr.commons.domain.RunState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ExecutableLine {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.FRANCE)
            .withZone(ZoneId.systemDefault());

    private final Block block;
    private final SimpleObjectProperty<RunState> runState = new SimpleObjectProperty<>(this, "runState",
            RunState.UNDEFINED);
    private final SimpleObjectProperty<Result> state = new SimpleObjectProperty<>(this, "state", Result.UNDEFINED);
    private final SimpleObjectProperty<Progress> progress = new SimpleObjectProperty<>(this, "progress", Progress.undefined());
    private final SimpleStringProperty cursor = new SimpleStringProperty(this, "cursor");

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
            if (RunState.UNDEFINED == rs) {
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

}
