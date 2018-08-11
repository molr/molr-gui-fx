package cern.lhc.app.seq.scheduler.execution.molr.conf;

import org.molr.commons.api.domain.AtomicIncrementMissionHandleFactory;
import org.molr.commons.api.domain.MissionHandleFactory;
import org.molr.server.api.Agency;
import org.molr.mole.api.Mole;
import cern.lhc.app.seq.scheduler.execution.molr.impl.LocalDelegationAgency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class MolrConfiguration {

    @Autowired
    private Set<Mole> moles;

    @Bean
    public MissionHandleFactory missionHandleFactory() {
        return new AtomicIncrementMissionHandleFactory();
    }

    @Bean
    public Agency agency(MissionHandleFactory missionHandleFactory) {
        return new LocalDelegationAgency(missionHandleFactory, moles);
    }

}
