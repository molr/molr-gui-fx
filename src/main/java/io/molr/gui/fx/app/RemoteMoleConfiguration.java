package io.molr.gui.fx.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import io.molr.gui.fx.util.CellFactories;
import io.molr.gui.fx.widgets.AvailableMissionsView;
import io.molr.mole.remote.conf.LocalhostRestClientConfiguration;

@Profile(value = { "remoteMole" })
@Import(LocalhostRestClientConfiguration.class)
@ComponentScan(basePackageClasses = {AvailableMissionsView.class, CellFactories.class})
public class RemoteMoleConfiguration {

}
