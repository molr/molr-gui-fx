package org.molr.gui.fx.widgets;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.molr.commons.domain.RunState;
import org.molr.commons.domain.Strand;

public class StrandLine {

    private final SimpleObjectProperty<RunState> runState;
    private final SimpleStringProperty id;
    private final Strand strand;

    public StrandLine(Strand strand, RunState runState) {
        this.strand = strand;
        this.runState = new SimpleObjectProperty<>(this, "result", runState);
        this.id = new SimpleStringProperty(this, "id", strand.id());
    }

    public SimpleObjectProperty<RunState> runStateProperty() {
        return runState;
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public Strand strand() {
        return strand;
    }
}
