/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx;

import io.molr.mole.remote.conf.LocalhostRestClientConfiguration;
import org.minifx.workbench.MiniFx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({LocalhostRestClientConfiguration.class})
public class MolrFxGuiMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(MolrFxGuiMain.class);

    public static void main(String[] args) {
        if (System.getProperty("spring.profiles.default") == null) {
            System.setProperty("spring.profiles.default", "demo");
        }

        LOGGER.info("Launching gui...");
        MiniFx.launcher(MolrFxGuiMain.class).launch(args);
    }

}
