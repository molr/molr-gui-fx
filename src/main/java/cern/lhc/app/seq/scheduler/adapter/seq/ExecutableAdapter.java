/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.adapter.seq;

import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import io.reactivex.Flowable;

public interface ExecutableAdapter {

    public Flowable<Open> openEvents();

    public Flowable<ResultChange> resultChanges();

    public Flowable<RunStateChange> runStateChanges();

}
