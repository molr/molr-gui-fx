package org.molr.gui.fx.conf;

import org.molr.gui.fx.info.ExecutableStatisticsProvider;
import org.molr.gui.fx.info.HardcodedExecutableStatisticsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
public class DemoConfiguration {

    @Bean
    public ExecutableStatisticsProvider executableStatisticsProvider() {
        return new HardcodedExecutableStatisticsProvider();
    }




}
