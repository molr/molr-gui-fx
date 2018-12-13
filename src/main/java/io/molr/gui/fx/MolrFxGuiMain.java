/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx;

import org.minifx.workbench.MiniFx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MolrFxGuiMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(MolrFxGuiMain.class);

    public static void main(String[] args) {
        if (System.getProperty("spring.profiles.default") == null) {
            System.setProperty("spring.profiles.default", "demo");
        }

        LOGGER.info("Launching gui...");
        MiniFx.launcher(LocalhostMolrGuiConfiguration.class).launch(args);
    }

}
