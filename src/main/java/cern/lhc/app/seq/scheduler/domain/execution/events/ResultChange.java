/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution.events;

import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public class ResultChange extends AbstractValueEvent<Result> {

    private ResultChange(ExecutionBlock block, Result value) {
        super(block, value);
    }

    public static ResultChange of(ExecutionBlock block, Result value) {
        return new ResultChange(block, value);
    }

    public static final ResultChange summaryFor(ExecutionBlock block, Iterable<Result> results) {
        return of(block, Result.summaryOf(results));
    }
}
