package cern.lhc.app.seq.scheduler.conf;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.adapter.seq.NoOpExecutableAdapter;
import cern.lhc.app.seq.scheduler.execution.molr.conf.MolrConfiguration;
import cern.lhc.app.seq.scheduler.execution.molr.impl.DemoMole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
@Import(MolrConfiguration.class)
public class DemoConfiguration {

    @Bean
    public DemoMole demoMole() {
        return new DemoMole();
    }

    @Bean
    public ExecutableAdapter executableAdapter() {
        return new NoOpExecutableAdapter();
    }
}
