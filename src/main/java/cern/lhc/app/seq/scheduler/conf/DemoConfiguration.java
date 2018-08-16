package cern.lhc.app.seq.scheduler.conf;

import cern.lhc.app.seq.scheduler.adapter.seq.ExecutableAdapter;
import cern.lhc.app.seq.scheduler.adapter.seq.ApplicationEventExecutableAdapter;
import org.molr.server.conf.LocalMolrConfiguration;
import org.molr.server.demo.DemoMole;
import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import cern.lhc.app.seq.scheduler.info.HardcodedExecutableStatisticsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
public class DemoConfiguration {

    @Bean
    public ExecutableAdapter executableAdapter() {
        return new ApplicationEventExecutableAdapter();
    }

    @Bean
    public ExecutableStatisticsProvider executableStatisticsProvider() {
        return new HardcodedExecutableStatisticsProvider();
    }
}
