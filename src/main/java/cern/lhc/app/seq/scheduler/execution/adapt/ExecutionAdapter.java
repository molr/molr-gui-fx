/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public interface ExecutionAdapter {

    void execute(ExecutionBlock block);

}
