package cern.lhc.app.seq.scheduler.execution.molr.impl;

import cern.lhc.app.seq.scheduler.domain.Result;
import cern.lhc.app.seq.scheduler.domain.RunState;
import cern.lhc.app.seq.scheduler.domain.execution.demo.DemoBlock;
import cern.lhc.app.seq.scheduler.domain.molr.*;
import cern.lhc.app.seq.scheduler.execution.molr.Mole;
import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import com.google.common.collect.ImmutableSet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DemoMole implements Mole {

    private final Set<Mission> dummyMissions = ImmutableSet.of(new Mission("Help Trump to win Elections."), new Mission("Conquer Rome"));


    @Override
    public Set<Mission> availableMissions() {
        return this.dummyMissions;
    }

    @Override
    public Mono<MissionDescription> representationOf(Mission mission) {
        return Mono.just(new DemoMissionDescription(dummyTree(mission)));
    }

    @Override
    public void instantiate(MissionHandle handle, Mission mission, Map<String, Object> params) {
        /* NOOP for the moment */
    }

    @Override
    public Flux<MissionState> statesFor(MissionHandle handle) {
        return Flux.empty();
    }

    @Override
    public void instruct(MissionHandle handle, MissionCommand command) {
        /* NOOP for the moment */
    }


    private DemoBlock dummyTree(Mission mission) {
        DemoBlock l1a = DemoBlock.ofName("Leaf 1A");
        DemoBlock l1b = DemoBlock.ofName("Leaf 1B");
        DemoBlock ss1 = DemoBlock.builder("subSeq 1").child(l1a).child(l1b).build();
        DemoBlock ss2 = DemoBlock.builder("subSeq 2").child(DemoBlock.ofName("Leaf 2A")).child(DemoBlock.ofName("Leaf 2B"))
                .build();
        return DemoBlock.builder(mission.name()).child(ss1).child(ss2).build();
    }
}
