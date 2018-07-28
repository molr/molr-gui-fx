/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.gui;

import cern.lhc.app.seq.scheduler.conf.DemoConfiguration;
import cern.lhc.app.seq.scheduler.execution.molr.conf.MolrConfiguration;
import org.minifx.workbench.MiniFx;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cern.lhc.app.seq.scheduler.conf.DeprecatedDemoConfiguration;

@Configuration
@ComponentScan
@Import({DemoConfiguration.class, MolrConfiguration.class})
public class SimpleSequenceSchedulerGui {

    public static void main(String[] args) {
        if (System.getProperty("spring.profiles.default") == null) {
            System.setProperty("spring.profiles.default", "demo");
        }

        MiniFx.launcher(SimpleSequenceSchedulerGui.class).launch(args);
    }

}
