package io.molr.gui.fx.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.widgets.AvailableMissionsView;
import io.molr.mole.core.runnable.conf.RunnableLeafMoleConfiguration;
import io.molr.mole.core.runnable.demo.conf.DemoRunnableLeafsConfiguration;

@Profile(value = { "localMole" })
@Import({RunnableLeafMoleConfiguration.class, DemoRunnableLeafsConfiguration.class})
@ComponentScan(basePackageClasses = {AvailableMissionsView.class, CellFactories.class})
public class LocalRunnableLeafsMoleConfiguration {
	
}
