/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.molr;

import java.util.Map;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.domain.molr.MissionCommand;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.domain.molr.MissionState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Agency {

    Mono<MissionDescription> representationOf(Mission mission);

    Mono<MissionHandle> instantiate(Mission mission, Map<String, Object> params);

    Flux<MissionState> statesFor(MissionHandle handle);

    void instruct(MissionHandle handle, MissionCommand command);

}
