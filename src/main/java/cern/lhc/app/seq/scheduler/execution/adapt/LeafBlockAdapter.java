/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class LeafBlockAdapter extends SingleBlockAdapter{

    public LeafBlockAdapter(ExecutionAdapter delegate, ExecutionBlock block) {
        super(delegate, block);
    }

    @Override
    protected void doWork() {
        delegate.execute(block);
    }

}
