/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui.commands;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class Open {

    private final ExecutionBlock executable;

    public Open(ExecutionBlock executable) {
        this.executable = executable;
    }

    public ExecutionBlock executable() {
        return executable;
    }

}
