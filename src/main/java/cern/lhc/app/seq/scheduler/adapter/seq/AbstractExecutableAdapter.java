/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.adapter.seq;

import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

/**
 * @author kfuchsbe
 */
public abstract class AbstractExecutableAdapter implements ExecutableAdapter {

    protected final PublishProcessor<Open> opens = PublishProcessor.create();
    protected final PublishProcessor<ResultChange> resultChanges = PublishProcessor.create();
    protected final PublishProcessor<RunStateChange> runStateChanges = PublishProcessor.create();

    @Override
    public Flowable<Open> openEvents() {
        return opens;
    }

    @Override
    public Flowable<ResultChange> resultChanges() {
        return resultChanges;
    }

    @Override
    public Flowable<RunStateChange> runStateChanges() {
        return runStateChanges;
    }
}