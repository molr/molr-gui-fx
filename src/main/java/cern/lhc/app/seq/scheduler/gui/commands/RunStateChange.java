/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import static java.util.Objects.requireNonNull;

import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class RunStateChange {

    private final ExecutionBlock executable;
    private final RunState runState;

    public RunStateChange(ExecutionBlock executable, RunState runState) {
        this.executable = requireNonNull(executable, "missionDescription must not be null");
        this.runState = requireNonNull(runState, "runState must not be null");
    }

    public RunState runState() {
        return runState;
    }

    public ExecutionBlock executable() {
        return executable;
    }

    @Override
    public String toString() {
        return "RunStateChange [missionDescription=" + executable + ", runState=" + runState + "]";
    }

}
