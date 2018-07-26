/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.execution.molr.impl;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Streams;

import cern.lhc.app.seq.scheduler.domain.molr.Mission;
import cern.lhc.app.seq.scheduler.domain.molr.MissionCommand;
import cern.lhc.app.seq.scheduler.domain.molr.MissionDescription;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandle;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandleFactory;
import cern.lhc.app.seq.scheduler.domain.molr.MissionState;
import cern.lhc.app.seq.scheduler.execution.molr.Agency;
import cern.lhc.app.seq.scheduler.execution.molr.Mole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This is probably the most simple agency inventible: It is employing several moles, instantiating a mission on the
 * first one who can do it
 *
 * @author kfuchsbe
 */
public class LocalDelegationAgency implements Agency {

    private final Map<Mission, Mole> missionMoles;
    private final ConcurrentMap<MissionHandle, Mole> activeMoles = new ConcurrentHashMap<>();
    private final MissionHandleFactory missionHandleFactory;

    public LocalDelegationAgency(MissionHandleFactory missionHandleFactory, Iterable<Mole> moles) {
        this.missionHandleFactory = requireNonNull(missionHandleFactory, "missionHandleFactory must not be null");
        requireNonNull(moles, "moles must not be null");
        this.missionMoles = scanMoles(moles);
    }

    @Override
    public Flux<Mission> executableMissions() {
        return Flux.fromIterable(missionMoles.keySet()).sort(comparing(Mission::name));
    }

    @Override
    public Mono<MissionDescription> representationOf(Mission mission) {
        return missionMoles.get(mission).representationOf(mission);
    }

    @Override
    public Mono<MissionHandle> instantiate(Mission mission, Map<String, Object> params) {
        return Mono.fromSupplier(() -> {
            MissionHandle handle = missionHandleFactory.next();
            Mole mole = missionMoles.get(mission);
            activeMoles.put(handle, mole);
            mole.instantiate(handle, mission, params);
            return handle;
        });
    }

    @Override
    public Flux<MissionState> statesFor(MissionHandle handle) {
        return activeMoles.get(handle).statesFor(handle);
    }

    @Override
    public void instruct(MissionHandle handle, MissionCommand command) {
        activeMoles.get(handle).instruct(handle, command);
    }

    private static final Map<Mission, Mole> scanMoles(Iterable<Mole> moles) {
        Builder<Mission, Mole> builder = ImmutableMap.builder();
        for (Mole mole : moles) {
            for (Mission mission : mole.availableMissions()) {
                builder.put(mission, mole);
            }
        }
        return builder.build();
    }

}
