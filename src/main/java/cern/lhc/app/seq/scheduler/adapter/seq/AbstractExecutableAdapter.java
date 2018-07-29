/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.adapter.seq;

import cern.lhc.app.seq.scheduler.gui.commands.ViewMission;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

/**
 * @author kfuchsbe
 */
public abstract class AbstractExecutableAdapter implements ExecutableAdapter {

    protected final EmitterProcessor<ViewMission> opens = EmitterProcessor.create();
    protected final EmitterProcessor<ResultChange> resultChanges = EmitterProcessor.create();
    protected final EmitterProcessor<RunStateChange> runStateChanges = EmitterProcessor.create();

    @Override
    public Flux<ViewMission> openEvents() {
        return opens;
    }

    @Override
    public Flux<ResultChange> resultChanges() {
        return resultChanges;
    }

    @Override
    public Flux<RunStateChange> runStateChanges() {
        return runStateChanges;
    }
}