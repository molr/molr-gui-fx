/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import java.util.Map;
import java.util.Set;

import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.ExecutionBlock;
import com.google.common.collect.SetMultimap;

public interface MissionState {

    /**
     * Retrieves the allowed commands for the given strand.
     *
     * @param strand the strand for which to query the allowed commands
     * @return a set of commands that are currently allowed.
     */
    Set<MissionCommand> allowedCommandsFor(Strand strand);

    ExecutionBlock cursorPositionIn(Strand strand);

    RunState runStateOf(Strand strand);

    Set<Strand> activeStrands();

}
