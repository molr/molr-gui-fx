package io.molr.gui.fx.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.widgets.AvailableMissionsView;
import io.molr.mole.remote.conf.LocalhostRestClientConfiguration;

//@Import({RunnableLeafMoleConfiguration.class, DemoRunnableLeafsConfiguration.class})
@Import(LocalhostRestClientConfiguration.class)
@ComponentScan(basePackageClasses = {AvailableMissionsView.class, CellFactories.class})
public class LocalRunnableLeafsMoleConfiguration {
	
}
