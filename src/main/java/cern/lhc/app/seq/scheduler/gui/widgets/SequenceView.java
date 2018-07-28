/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static org.minifx.workbench.domain.PerspectivePos.CENTER;

import cern.lhc.app.seq.scheduler.gui.perspectives.MissionsPerspective;
import org.minifx.workbench.annotations.View;
import org.minifx.workbench.domain.PerspectivePos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.gui.commands.Open;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

@View(at = CENTER, in = MissionsPerspective.class)
@Component
public abstract class SequenceView extends BorderPane {

    private final TabPane tabPane;

    public SequenceView(@Autowired ExecutableAdapter executableAdapter) {
        tabPane = new TabPane();
        setCenter(tabPane);
        executableAdapter.openEvents().subscribeOn(fxThread()).subscribe(this::update);
    }

    public void update(Open openExecutable) {
        SequencePane seqPane = sequencePane(openExecutable.executable());
        Tab tab = new Tab(openExecutable.executable().name(), seqPane);
        tabPane.getTabs().add(tab);
    }

    @Lookup
    public abstract SequencePane sequencePane(ExecutionBlock executable);
}
