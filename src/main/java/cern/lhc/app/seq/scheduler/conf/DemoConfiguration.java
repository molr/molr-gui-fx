package cern.lhc.app.seq.scheduler.conf;

import cern.lhc.app.seq.scheduler.info.ExecutableStatisticsProvider;
import cern.lhc.app.seq.scheduler.info.HardcodedExecutableStatisticsProvider;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
