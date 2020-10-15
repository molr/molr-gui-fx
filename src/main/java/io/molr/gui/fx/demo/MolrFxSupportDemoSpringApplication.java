package io.molr.gui.fx.demo;

import io.molr.gui.fx.conf.MolrFxSupportConfiguration;
import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.mole.core.runnable.conf.RunnableLeafMoleConfiguration;
import io.molr.mole.core.runnable.demo.conf.DemoRunnableLeafsConfiguration;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.minifx.fxcommons.SingleSceneSpringJavaFxApplication.applicationLauncher;

@Configuration
@Import({DemoRunnableLeafsConfiguration.class, RunnableLeafMoleConfiguration.class, MolrFxSupportConfiguration.class})
public class MolrFxSupportDemoSpringApplication {

    @Autowired
    private MolrFxSupport support;

    public static void main(String... args) {
        applicationLauncher().configurationClasses(MolrFxSupportDemoSpringApplication.class).launch();
    }

    @Bean
    public Scene mainScene() {
        return UiCreation.createScene(support);
    }


}
