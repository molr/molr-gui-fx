/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution;

public interface ExecutionEvent {
    
    ExecutionBlock block();
    
}
