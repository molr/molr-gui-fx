/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.adapter.seq;

import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import reactor.core.publisher.Flux;

public interface ExecutableAdapter {

    public Flux<Open> openEvents();

    public Flux<ResultChange> resultChanges();

    public Flux<RunStateChange> runStateChanges();

}
