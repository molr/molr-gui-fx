/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cern.lhc.app.seq.scheduler.adapter.seq.AbstractExecutableAdapter;
import cern.lhc.app.seq.scheduler.adapter.seq.DummyExecutableAdapter;
import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import cern.lhc.app.seq.scheduler.info.HardcodedExecutableStatisticsProvider;

@Configuration
@Profile("demo")
public class DemoConfiguration {

    @Bean(initMethod = "init")
    public AbstractExecutableAdapter dummyExecutableAdapter() {
        return new DummyExecutableAdapter();
    }

    @Bean
    public ExecutableStatisticsProvider executableStatisticsProvider() {
        return new HardcodedExecutableStatisticsProvider();
    }
}
