package cern.lhc.app.seq.scheduler.execution.molr.conf;

import cern.lhc.app.seq.scheduler.domain.molr.AtomicIncrementMissionHandleFactory;
import cern.lhc.app.seq.scheduler.domain.molr.MissionHandleFactory;
import cern.lhc.app.seq.scheduler.execution.molr.Agency;
import cern.lhc.app.seq.scheduler.execution.molr.Mole;
import cern.lhc.app.seq.scheduler.execution.molr.MolrService;
import cern.lhc.app.seq.scheduler.execution.molr.impl.LocalDelegationAgency;
import cern.lhc.app.seq.scheduler.execution.molr.impl.SingleAgencyMolrService;
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
    public Agency localAgency(MissionHandleFactory missionHandleFactory) {
        return new LocalDelegationAgency(missionHandleFactory, moles);
    }

    @Bean
    public MolrService molrService(Agency agency) {
        return new SingleAgencyMolrService(agency);
    }


}
