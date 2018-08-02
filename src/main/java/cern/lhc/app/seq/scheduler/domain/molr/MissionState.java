/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.Map;
import java.util.Set;

import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;

public interface MissionState {

    /**
     * Determines if the given command is allowed on the given strand.
     *
     * @param command the command to check if it is allowed
     * @param strand  the strand on which to check if the command is allowed
     * @return {@code true} if the given command is allowed to be executed on the given strand, {@code false} otherwise
     */
    boolean isAllowed(MissionCommand command, Strand strand);

    Map<Strand, ExecutionBlock> cursorPositions();

    Map<Strand, RunState> activeStrandStates();
}
