package io.molr.gui.fx.demo;

import io.molr.commons.domain.Mission;
import io.molr.gui.fx.conf.MolrFxSupportConfiguration;
import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.gui.fx.support.SimpleMissionControl;
import io.molr.mole.core.runnable.conf.RunnableLeafMoleConfiguration;
import io.molr.mole.core.runnable.demo.conf.DemoRunnableLeafsConfiguration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.minifx.fxcommons.SingleSceneSpringJavaFxApplication.applicationLauncher;

@Configuration
@Import({DemoRunnableLeafsConfiguration.class, RunnableLeafMoleConfiguration.class, MolrFxSupportConfiguration.class})
public class MolrFxSupportDemoApplication {

    @Autowired
    private MolrFxSupport support;

    public static void main(String... args) {
        applicationLauncher().configurationClasses(MolrFxSupportDemoApplication.class).launch();
    }

    @Bean
    public Scene mainScene() {
        return UiCreation.createScene(support);
    }


}
