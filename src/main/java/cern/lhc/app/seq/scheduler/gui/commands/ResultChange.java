/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import static java.util.Objects.requireNonNull;

import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class ResultChange {
    private final ExecutionBlock executable;
    private final Result result;

    public ResultChange(ExecutionBlock executable, Result result) {
        this.executable = requireNonNull(executable, "executable must not be null");
        this.result = requireNonNull(result, "result must not be null");
    }

    public Result result() {
        return result;
    }

    public ExecutionBlock executable() {
        return executable;
    }
}
