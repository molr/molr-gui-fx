/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.events;

import org.molr.commons.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public final class RunStateChange extends AbstractValueEvent<RunState> {

    private RunStateChange(ExecutionBlock block, RunState value) {
        super(block, value);
    }

    public static RunStateChange of(ExecutionBlock block, RunState value) {
        return new RunStateChange(block, value);
    }

    public static RunStateChange summaryFor(ExecutionBlock block, Iterable<RunState> rs) {
        return RunStateChange.of(block, RunState.summaryOf(rs));
    }

}
