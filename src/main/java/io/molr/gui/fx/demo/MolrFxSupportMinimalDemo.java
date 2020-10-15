package io.molr.gui.fx.demo;

import io.molr.gui.fx.MolrFxSupportFactory;
import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.mole.core.runnable.conf.RunnableLeafMoleConfiguration;
import io.molr.mole.core.runnable.demo.conf.DemoRunnableLeafsConfiguration;
import javafx.scene.Scene;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.minifx.fxcommons.SingleSceneSpringJavaFxApplication.applicationLauncher;

@Configuration
public class MolrFxSupportMinimalDemo {

    public static void main(String... args) {
        applicationLauncher().configurationClasses(MolrFxSupportMinimalDemo.class).launch();
    }

    @Bean
    public Scene mainScene() {
        MolrFxSupport support = MolrFxSupportFactory.newMolrFxSupport(DemoRunnableLeafsConfiguration.class, RunnableLeafMoleConfiguration.class);
        return UiCreation.createScene(support);
    }
}
