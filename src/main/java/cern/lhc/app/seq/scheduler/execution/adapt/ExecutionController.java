/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.adapt;

import java.util.function.Consumer;

import org.molr.commons.api.domain.RunState;

/**
 * Intended to be injected into the executors in order to react on commands.
 * 
 * @author kfuchsbe
 */
public interface ExecutionController {

    /**
     * Blocks until the the state is running. The passed in consumer will be called twice at maximum: Once with the
     * state when entering the method and once with the state before leaving.
     */
    public void awaitNonPaused(Consumer<RunState> state);

}
