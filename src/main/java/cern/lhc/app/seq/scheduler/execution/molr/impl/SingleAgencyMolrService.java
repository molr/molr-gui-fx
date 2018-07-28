package cern.lhc.app.seq.scheduler.execution.molr.impl;

import cern.lhc.app.seq.scheduler.domain.molr.*;
import cern.lhc.app.seq.scheduler.execution.molr.Agency;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SingleAgencyMolrService implements MolrService {

    private final Agency agency;

    public SingleAgencyMolrService(Agency agency) {
        this.agency = requireNonNull(agency, "delegatingAgency must not be null");
    }

    @Override
    public Flux<AgencyState> states() {
        return agency.states();
    }

    @Override
    public Flux<Mission> executableMissions() {
        return agency.executableMissions();
    }

    @Override
    public Mono<MissionDescription> representationOf(Mission mission) {
        return agency.representationOf(mission);
    }

    @Override
    public Mono<MissionHandle> instantiate(Mission mission, Map<String, Object> params) {
        return agency.instantiate(mission, params);
    }

    @Override
    public Flux<MissionState> statesFor(MissionHandle handle) {
        return agency.statesFor(handle);
    }

    @Override
    public void instruct(MissionHandle handle, MissionCommand command) {
        agency.instruct(handle, command);
    }
}
