/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static org.molr.commons.domain.RunState.FINISHED;
import static org.molr.commons.domain.RunState.RUNNING;
import static cern.lhc.app.seq.scheduler.util.DurationFormats.shortLetters;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import cern.lhc.app.seq.scheduler.domain.Result;
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

    private final Block executable;
    private final SimpleObjectProperty<RunState> runState = new SimpleObjectProperty<>(this, "runState",
            RunState.UNDEFINED);
    private final SimpleObjectProperty<Result> state = new SimpleObjectProperty<>(this, "state", Result.UNDEFINED);
    private final SimpleObjectProperty<Double> progress = new SimpleObjectProperty<>(this, "progress", -1.0);
    private final SimpleStringProperty comment = new SimpleStringProperty(this, "comment", "");
    private final SimpleObjectProperty<Duration> usualDuration = new SimpleObjectProperty<>(this, "usualDuration");
    private final SimpleObjectProperty<Instant> startedAt = new SimpleObjectProperty<>(this, "startedAt");
    private final SimpleObjectProperty<Instant> endedAt = new SimpleObjectProperty<>(this, "endedAt");
    private final SimpleObjectProperty<Instant> actualTime = new SimpleObjectProperty<>(this, "actualTime");
    private final SimpleObjectProperty<Duration> elapsedDuration = new SimpleObjectProperty<>(this, "elapsedDuration");
    private final SimpleStringProperty cursor = new SimpleStringProperty(this, "cursor");

    private final ReadOnlyStringProperty name;

    public <T> ExecutableLine(Block executable) {
        this.executable = Objects.requireNonNull(executable, "missionDescription must not be null");
        this.name = new ReadOnlyStringWrapper(this, "name", executable.text());

        configureAsLeaf();
    }

    private void configureAsLeaf() {

        comment.bind(Bindings.createStringBinding(() -> {
            RunState rs = runStateProperty().getValue();
            if (RunState.UNDEFINED == rs) {
                return "";
            }

            Duration elapsed = elapsedDuration.getValue();
            if (elapsed == null) {
                return "";
            }

            if (RunState.FINISHED == rs) {
                return "finished after " + shortLetters(elapsed);
            }

            Duration expected = usualDurationProperty().getValue();
            if (expected == null) {
                return "" + shortLetters(elapsed) + " elapsed";
            }

            Duration stillToGo = expected.minus(elapsed);
            if (stillToGo.isNegative()) {
                return shortLetters(stillToGo.negated()) + " longer than usual";
            } else {
                return "" + shortLetters(elapsed) + " elapsed; " + shortLetters(stillToGo) + " still to go; ETA: "
                        + formatter.format(Instant.now().plus(stillToGo));
            }
        }, usualDurationProperty(), elapsedDuration, runStateProperty()));

        progress.bind(Bindings.createObjectBinding(() -> {
            RunState rs = runStateProperty().getValue();
            if (RunState.UNDEFINED == rs) {
                return 0.0;
            } else if (FINISHED == rs) {
                return 1.0;
            }

            Duration expected = usualDurationProperty().getValue();
            Duration elapsed = elapsedDuration.getValue();
            if ((expected == null) || (elapsed == null)) {
                return -1.0;
            }

            if (expected.minus(elapsed).isNegative()) {
                return -1.0;
            }

            return (1.0 * elapsed.toMillis()) / (1.0 * expected.toMillis());
        }, usualDurationProperty(), elapsedDuration, runStateProperty()));

        runState.addListener((observable, oldValue, newValue) -> {
            /*
             * We are very lenient here ... Each time, the line is put to running again, basically the original start
             * time is erased. To be seen if this is a good approach. This anyway only makes sense for stand-alone lines
             * and not such, which are a summary of others.
             */

            Instant now = Instant.now();
            if (RUNNING == newValue) {
                startedAt.set(now);
                endedAt.set(null);
                elapsedDuration.set(null);
            } else if (FINISHED == newValue) {
                endedAt.set(now);
                Instant start = startedAt.get();
                if (start != null) {
                    elapsedDuration.set(Duration.between(start, now));
                }
            }
        });

        actualTimeProperty().addListener((observable, oldValue, newValue) -> {

            Instant start = startedAt.get();
            if (start == null) {
                return;
            }

            Instant end = endedAt.get();
            if (end != null) {
                return;
            }

            elapsedDuration.set(Duration.between(start, newValue));

        });
    }

    public Block executable() {
        return executable;
    }

    public SimpleObjectProperty<Result> stateProperty() {
        return state;
    }

    public ReadOnlyStringProperty nameProperty() {
        return this.name;
    }

    public SimpleObjectProperty<Double> progressProperty() {
        return this.progress;
    }

    public SimpleStringProperty commentProperty() {
        return this.comment;
    }

    public SimpleObjectProperty<Duration> usualDurationProperty() {
        return usualDuration;
    }

    public SimpleObjectProperty<Instant> actualTimeProperty() {
        return actualTime;
    }

    public SimpleObjectProperty<RunState> runStateProperty() {
        return runState;
    }

    public SimpleStringProperty cursorProperty() {
        return cursor;
    }

}
