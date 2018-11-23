/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.molr.gui.fx;

import org.molr.agency.remote.conf.LocalhostRestClientConfiguration;
import org.minifx.workbench.MiniFx;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({LocalhostRestClientConfiguration.class})
public class MolrFxGuiMain {

    public static void main(String[] args) {
        if (System.getProperty("spring.profiles.default") == null) {
            System.setProperty("spring.profiles.default", "demo");
        }

        MiniFx.launcher(MolrFxGuiMain.class).launch(args);
    }

}
