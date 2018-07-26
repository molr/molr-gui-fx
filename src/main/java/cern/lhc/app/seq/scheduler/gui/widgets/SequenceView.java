/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.widgets;

import static io.reactivex.rxjavafx.schedulers.JavaFxScheduler.platform;

import org.minifx.workbench.annotations.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import cern.lhc.app.seq.scheduler.gui.commands.Open;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

@View
@Component
public abstract class SequenceView extends BorderPane {

    private final TabPane tabPane;

    public SequenceView(@Autowired ExecutableAdapter executableAdapter) {
        tabPane = new TabPane();
        setCenter(tabPane);
        executableAdapter.openEvents().observeOn(platform()).subscribe(this::update);
    }

    public void update(Open openExecutable) {
        SequencePane seqPane = sequencePane(openExecutable.executable());
        Tab tab = new Tab(openExecutable.executable().name(), seqPane);
        tabPane.getTabs().add(tab);
    }

    @Lookup
    public abstract SequencePane sequencePane(ExecutionBlock executable);
}
